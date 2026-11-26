package dev.gallerybridge

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.RandomAccessFile
import org.json.JSONObject

internal class MotionExport(private val context: Context, private val ledger: Ledger) {
    private val documents = Documents(context)

    fun pair(photo: Item): Item? {
        if (photo.mime != "image/jpeg" || ledger.restricted(photo.sha) || ledger.excluded(photo.sha)) return null
        val id = photo.metadata?.let(::JSONObject)?.optString("live_id")?.let(::normalizeLiveId) ?: return null
        val match = ledger.livePair(photo.sha, id, false) ?: return null
        val video = ledger.item(match.sha) ?: return null
        return video.takeIf { it.mime in setOf("video/mp4", "video/quicktime") &&
            !ledger.restricted(it.sha) && !ledger.excluded(it.sha) }
    }

    fun export(photo: Item, destination: Uri, reserve: Long, check: () -> Unit) {
        val video = pair(photo) ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        demand(photo.size + video.size <= 256 * 1024 * 1024 && photo.size + video.size < context.cacheDir.usableSpace - reserve,
            Reason.SPACE)
        val stillFile = File.createTempFile("motion-still-", ".jpg", context.cacheDir)
        val videoFile = File.createTempFile("motion-video-", ".mov", context.cacheDir)
        try {
            val still = extract(photo, stillFile, reserve, check)
            val motion = extract(video, videoFile, reserve, check)
            demand(ExifInterface(stillFile).getAttribute(ExifInterface.TAG_XMP).isNullOrEmpty(), Reason.DESTINATION_REVIEW)
            RandomAccessFile(stillFile, "r").use {
                demand(it.length() >= 4, Reason.INTEGRITY)
                it.seek(it.length() - 2)
                demand(it.readUnsignedShort() == 0xffd9, Reason.DESTINATION_REVIEW)
            }
            RandomAccessFile(videoFile, "r").use {
                demand(it.length() >= 16, Reason.INTEGRITY)
                it.seek(4)
                demand(it.readInt() == 0x66747970, Reason.DESTINATION_REVIEW)
            }
            val resolver = context.contentResolver
            val out = resolver.openOutputStream(destination, "wt") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
            out.use { output ->
                stillFile.inputStream().use { image ->
                    videoFile.inputStream().use { movie ->
                        writeJpegMotionPhoto(image, still, movie, motion, video.mime, output, check)
                    }
                }
            }
            val readBack = resolver.openInputStream(destination) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
            readBack.use { input ->
                val exif = ExifInterface(input)
                val xmp = exif.getAttribute(ExifInterface.TAG_XMP)
                demand(xmp?.contains("Item:Length=\"${motion.bytes}\"") == true, Reason.INTEGRITY)
            }
            resolver.openInputStream(destination)?.use { input ->
                val total = digest(input, maxBytes = still.bytes + motion.bytes + 4096, checkpoint = check).bytes
                val reread = resolver.openInputStream(destination) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
                reread.use {
                    var left = total - motion.bytes
                    demand(left > still.bytes, Reason.INTEGRITY)
                    while (left > 0) {
                        check()
                        val skipped = it.skip(left)
                        demand(skipped > 0, Reason.INTEGRITY)
                        left -= skipped
                    }
                    demand(digest(it, maxBytes = motion.bytes, checkpoint = check) == motion, Reason.INTEGRITY)
                }
            } ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        } finally {
            stillFile.delete()
            videoFile.delete()
        }
    }

    private fun extract(item: Item, file: File, reserve: Long, check: () -> Unit): Digest =
        documents.seekable(Uri.parse(item.source.uri), check, reserve) { channel ->
            Archive(channel).use { zip ->
                val member = zip.member(item.ordinal)
                demand(member.path == item.path && !member.restricted && member.crc == item.crc &&
                    member.size == item.size, Reason.SOURCE_CHANGED)
                file.outputStream().use { zip.verify(member, it, check) }.also {
                    demand(it.sha == item.sha, Reason.INTEGRITY)
                }
            }
        }
}
