package dev.gallerybridge

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.SeekableByteChannel
import java.security.MessageDigest
import java.time.Instant
import java.util.Locale
import java.util.zip.CRC32
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile

const val GIB = 1_073_741_824L
const val BUFFER_SIZE = 256 * 1024

enum class Reason {
    INVALID_ARCHIVE, UNSAFE_ENTRY, INTEGRITY, SOURCE_CHANGED, NEEDS_USER_ACTION, SPACE,
    WIFI_REQUIRED, BATTERY, THERMAL, PAUSED, DESTINATION_REVIEW, UNSUPPORTED
}

class BridgeFailure(val reason: Reason) : IOException(reason.name)

fun demand(condition: Boolean, reason: Reason) {
    if (!condition) throw BridgeFailure(reason)
}

data class Digest(val sha: String, val bytes: Long, val crc: Long)

fun digest(
    input: InputStream,
    output: OutputStream? = null,
    maxBytes: Long = Long.MAX_VALUE,
    checkpoint: () -> Unit = {}
): Digest {
    val hash = MessageDigest.getInstance("SHA-256")
    val crc = CRC32()
    var bytes = 0L
    val buffer = ByteArray(BUFFER_SIZE)
    while (true) {
        checkpoint()
        val count = input.read(buffer)
        if (count < 0) break
        demand(count > 0, Reason.INTEGRITY)
        demand(count.toLong() <= maxBytes - bytes, Reason.INTEGRITY)
        bytes += count
        hash.update(buffer, 0, count)
        crc.update(buffer, 0, count)
        output?.write(buffer, 0, count)
    }
    val bytesOfHash = hash.digest()
    val digits = "0123456789abcdef"
    val hex = CharArray(bytesOfHash.size * 2)
    for (index in bytesOfHash.indices) {
        val value = bytesOfHash[index].toInt() and 0xff
        hex[index * 2] = digits[value ushr 4]
        hex[index * 2 + 1] = digits[value and 0xf]
    }
    return Digest(hex.concatToString(), bytes, crc.value)
}

data class Member(
    val ordinal: Int, val path: String, val size: Long, val crc: Long,
    val mime: String?, val modified: Instant?, val restricted: Boolean
) {
    val directory get() = path.substringBeforeLast('/', "")
    val name get() = path.substringAfterLast('/')
    val sidecar get() = name.endsWith(".json", true)
}

class Archive(channel: SeekableByteChannel) : AutoCloseable {
    private val zip = ZipFile.builder().setSeekableByteChannel(channel).get()
    private val entries = mutableListOf<ZipArchiveEntry>()
    private val noMedia = mutableSetOf<String>()

    init {
        try {
            var expanded = 0L
            val source = zip.entries
            while (source.hasMoreElements()) {
                val entry = source.nextElement()
                demand(entries.size < 250_000, Reason.UNSAFE_ENTRY)
                validatePath(entry.name, entry.isDirectory)
                val fileType = entry.unixMode and 0xf000
                demand(!entry.isUnixSymlink && fileType in setOf(0, 0x4000, 0x8000) &&
                    zip.canReadEntryData(entry), Reason.UNSAFE_ENTRY)
                demand(entry.size in 0..(256 * GIB) && entry.crc >= 0, Reason.INVALID_ARCHIVE)
                if (entry.size > 64 * 1024 * 1024L) {
                    demand(entry.compressedSize >= (entry.size + 499) / 500, Reason.UNSAFE_ENTRY)
                }
                expanded += entry.size
                demand(expanded <= 2_048 * GIB, Reason.UNSAFE_ENTRY)
                entries.add(entry)
                if (entry.name.substringAfterLast('/').equals(".nomedia", true)) {
                    noMedia.add(entry.name.substringBeforeLast('/', "").lowercase(Locale.ROOT))
                }
            }
            demand(entries.isNotEmpty(), Reason.INVALID_ARCHIVE)
        } catch (failure: Exception) {
            zip.close()
            throw failure
        }
    }

    val count get() = entries.size

    fun member(ordinal: Int): Member {
        val entry = entries[ordinal]
        return Member(
            ordinal, entry.name, entry.size, entry.crc,
            if (entry.isDirectory) null else mimeOf(entry.name),
            entry.lastModifiedTime?.toInstant(),
            restrictedPath(entry.name) || underNoMedia(entry.name)
        )
    }

    private fun underNoMedia(path: String): Boolean {
        var parent = path.substringBeforeLast('/', "").lowercase(Locale.ROOT)
        while (parent.isNotEmpty()) {
            if (parent in noMedia) return true
            parent = parent.substringBeforeLast('/', "")
        }
        return "" in noMedia
    }

    fun open(ordinal: Int): InputStream = zip.getInputStream(entries[ordinal])

    fun verify(member: Member, output: OutputStream? = null, checkpoint: () -> Unit = {}): Digest {
        demand(member == member(member.ordinal), Reason.SOURCE_CHANGED)
        val result = open(member.ordinal).use { digest(it, output, member.size, checkpoint) }
        demand(result.bytes == member.size && result.crc == member.crc, Reason.INTEGRITY)
        return result
    }

    override fun close() = zip.close()
}

fun validatePath(path: String, directory: Boolean = false) {
    val trimmed = if (directory) path.removeSuffix("/") else path
    demand(path.length <= 4096 && path.none { it.isISOControl() || it == '\\' } &&
        !Regex("^[A-Za-z]:").containsMatchIn(path) &&
        trimmed.split('/').none { it.isEmpty() || it == "." || it == ".." }, Reason.UNSAFE_ENTRY)
}

fun restrictedPath(path: String): Boolean = path.lowercase(Locale.ROOT).split('/').any {
    it.startsWith('.') || it in setOf("locked folder", "pasta trancada", "pasta segura",
        "secure folder", "private album", "álbum privado", "lixeira", "trash")
}

private val mediaTypes = mapOf(
    "jpg" to "image/jpeg", "jpeg" to "image/jpeg", "png" to "image/png",
    "webp" to "image/webp", "gif" to "image/gif", "heic" to "image/heic",
    "heif" to "image/heif", "avif" to "image/avif", "dng" to "image/x-adobe-dng",
    "mp4" to "video/mp4", "mov" to "video/quicktime", "m4v" to "video/mp4",
    "3gp" to "video/3gpp", "mkv" to "video/x-matroska", "webm" to "video/webm"
)

fun mimeOf(path: String): String? = mediaTypes[path.substringAfterLast('.', "").lowercase(Locale.ROOT)]

fun sidecarBase(name: String): String? = when {
    name.endsWith(".supplemental-metadata.json", true) ->
        name.dropLast(".supplemental-metadata.json".length)
    name.endsWith(".json", true) -> name.dropLast(5)
    else -> null
}
