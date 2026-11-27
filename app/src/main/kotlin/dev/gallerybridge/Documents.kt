package dev.gallerybridge

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import android.util.Xml
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser

internal class Documents(private val context: Context) {
    private val resolver = context.contentResolver

    fun open(uri: Uri): InputStream = resolver.openInputStream(uri) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)

    fun <T> seekable(uri: Uri, checkpoint: () -> Unit, reserve: Long, use: (FileChannel) -> T): T {
        checkpoint()
        val descriptor = resolver.openFileDescriptor(uri, "r") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        val positionable = try {
            Os.lseek(descriptor.fileDescriptor, 0, OsConstants.SEEK_SET)
            true
        } catch (_: ErrnoException) {
            false
        }
        if (positionable) {
            return android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { use(it.channel) }
        }
        descriptor.close()
        val expectedSize = resolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use {
            if (it.moveToFirst() && !it.isNull(0)) it.getLong(0) else null
        }
        val budget = context.cacheDir.usableSpace - reserve
        demand(budget > 0 && (expectedSize == null || expectedSize in 1..budget), Reason.SPACE)
        // Each verification can run independently of the other selected ZIPs.
        val spool = File.createTempFile("bridge-archive-", ".spool", context.cacheDir)
        try {
            open(uri).use { input ->
                spool.outputStream().use { output ->
                    val result = digest(input, output, budget) {
                        checkpoint()
                        demand(context.cacheDir.usableSpace > reserve, Reason.SPACE)
                    }
                    demand(expectedSize == null || expectedSize == result.bytes, Reason.SOURCE_CHANGED)
                }
            }
            return RandomAccessFile(spool, "r").use { use(it.channel) }
        } finally {
            spool.delete()
        }
    }
}

internal fun captureMetadata(archive: Archive, member: Member, checkpoint: () -> Unit): JSONObject? {
    val mime = member.mime ?: return null
    checkpoint()
    return archive.open(member.ordinal).buffered().use { raw ->
        val input = object : FilterInputStream(raw) {
            override fun read(): Int { checkpoint(); return super.read() }
            override fun read(b: ByteArray, off: Int, len: Int): Int {
                checkpoint()
                return super.read(b, off, minOf(len, BUFFER_SIZE))
            }
            override fun skip(n: Long): Long { checkpoint(); return super.skip(minOf(n, BUFFER_SIZE.toLong())) }
        }
        raw.mark(64)
        val header = ByteArray(32)
        var length = 0
        while (length < header.size) {
            val n = input.read(header, length, header.size - length)
            if (n < 0) break
            demand(n > 0, Reason.INTEGRITY)
            length += n
        }
        raw.reset()
        val bytes = header.copyOf(length)
        val stillMime = if (mime in setOf("image/heic", "image/heif") && verifiedFormat(bytes, "image/jpeg"))
            "image/jpeg" else mime
        val format = verifiedFormat(bytes, stillMime)
        try {
            val result = JSONObject().put("format_verified", if (format && stillMime == mime) 1 else 0)
            if (stillMime != mime) result.put("actual_mime", stillMime)
            if (!format) return@use result
            when (stillMime) {
                "video/mp4", "video/quicktime", "video/3gpp" -> {
                    val facts = inspectQuickTime(input, member.size)
                    result.put("keys", facts.keys?.json()).put("container", facts.container?.json())
                        .put("live_id", facts.contentId)
                }
                "image/jpeg", "image/png", "image/heic", "image/heif", "image/webp", "image/x-adobe-dng" -> {
                    val exif = ExifInterface(input)
                    exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let {
                        result.put("exif", cameraTime(it, exif.getAttribute(ExifInterface.TAG_OFFSET_TIME_ORIGINAL),
                            "ExifIFD:DateTimeOriginal").json())
                    }
                    exif.latLong?.let { result.put("latitude", it[0]).put("longitude", it[1]) }
                    result.put("orientation", exif.getAttribute(ExifInterface.TAG_ORIENTATION))
                    result.put("description", exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)?.take(65_536))
                    val pairIds = listOfNotNull(
                        appleMakerNoteId(exif.getAttributeBytes(ExifInterface.TAG_MAKER_NOTE)),
                        appleXmpId(exif.getAttribute(ExifInterface.TAG_XMP))
                    ).distinct()
                    result.put("live_id", pairIds.singleOrNull())
                }
            }
            result
        } catch (failure: BridgeFailure) {
            throw failure
        } catch (_: IllegalArgumentException) {
            JSONObject().put("read_error", true).put("format_verified", 0)
        } catch (_: IOException) {
            JSONObject().put("read_error", true).put("format_verified", 0)
        }
    }
}

private fun appleXmpId(text: String?): String? {
    if (text.isNullOrBlank() || text.length > 256 * 1024 || "<!DOCTYPE" in text) return null
    return runCatching {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(text.reader())
        val ids = mutableSetOf<String>()
        val namespace = "http://ns.apple.com/quicktime/1.0/"
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) continue
            normalizeLiveId(parser.getAttributeValue(namespace, "ContentIdentifier"))?.let(ids::add)
            if (parser.namespace == namespace && parser.name == "ContentIdentifier") {
                normalizeLiveId(parser.nextText())?.let(ids::add)
            }
        }
        ids.singleOrNull()
    }.getOrNull()
}

private fun appleMakerNoteId(bytes: ByteArray?): String? {
    if (bytes == null || bytes.size < 16 || bytes.copyOfRange(0, 10).toString(Charsets.US_ASCII) != "Apple iOS\u0000")
        return null
    val input = java.nio.ByteBuffer.wrap(bytes)
    input.order(when (bytes.copyOfRange(12, 14).toString(Charsets.US_ASCII)) {
        "MM" -> java.nio.ByteOrder.BIG_ENDIAN
        "II" -> java.nio.ByteOrder.LITTLE_ENDIAN
        else -> return null
    })
    val count = input.getShort(14).toInt() and 0xffff
    if (count > (bytes.size - 16) / 12) return null
    return (0 until count).mapNotNull { ordinal ->
        val entry = 16 + ordinal * 12
        if (input.getShort(entry).toInt() != 17 || input.getShort(entry + 2).toInt() != 2) return@mapNotNull null
        val length = input.getInt(entry + 4)
        val offset = input.getInt(entry + 8)
        if (length !in 36..37 || offset !in 0..(bytes.size - length)) return@mapNotNull null
        normalizeLiveId(bytes.copyOfRange(offset, offset + length).toString(Charsets.US_ASCII).trimEnd('\u0000'))
    }.distinct().singleOrNull()
}

private fun TimeFact.json() = JSONObject().put("raw", raw).put("source", source)
    .put("basis", basis.name).put("epoch_ms", instant?.toEpochMilli()).put("wall", wall?.toString())
