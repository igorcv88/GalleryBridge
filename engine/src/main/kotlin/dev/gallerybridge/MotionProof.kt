package dev.gallerybridge

import java.io.File
import java.io.DataInputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.io.SequenceInputStream
import javax.xml.parsers.DocumentBuilderFactory

private const val XMP = "http://ns.adobe.com/xap/1.0/\u0000"
private const val CAMERA = "http://ns.google.com/photos/1.0/camera/"
private const val CONTAINER = "http://ns.google.com/photos/1.0/container/"
private const val ITEM = "http://ns.google.com/photos/1.0/container/item/"

data class MotionProof(
    val still: Digest, val video: Digest?, val total: Long, val existingXmp: Boolean,
    val motionOnlyXmp: Boolean = false, val videoStart: Long? = null
)

private data class JpegLayout(val end: Long, val xmpAt: Long, val afterXmp: Long, val xmp: String?)

private class JpegReader(input: InputStream) : FilterInputStream(input) {
    var position = 0L
        private set
    fun byte(): Int {
        val value = super.read()
        demand(value >= 0, Reason.INTEGRITY)
        position++
        return value
    }
    fun block(size: Int): ByteArray = ByteArray(size).also { buffer ->
        var offset = 0
        while (offset < size) {
            val count = super.read(buffer, offset, size - offset)
            demand(count > 0, Reason.INTEGRITY)
            offset += count
        }
        position += size
    }
}

private fun jpegLayout(channel: FileChannel, end: Long, check: () -> Unit): JpegLayout =
    JpegReader(channel.range(0, end).buffered()).use { input ->
    demand(input.byte() == 255 && input.byte() == 216, Reason.UNSUPPORTED)
    var entropy = false
    var frame = false
    var scan = false
    var xmp: String? = null
    var xmpAt = 0L
    var afterXmp = 0L
    var checkpointAt = 0L
    while (true) {
        if (input.position >= checkpointAt) { check(); checkpointAt = input.position + 4096 }
        val start = input.position
        if (input.byte() != 255) {
            demand(entropy, Reason.INTEGRITY)
            continue
        }
        var code = input.byte()
        while (code == 255) code = input.byte()
        if (entropy && (code == 0 || code in 208..215)) continue
        entropy = false
        if (code == 217) {
            demand(frame && scan, Reason.INTEGRITY)
            return@use JpegLayout(input.position, xmpAt, afterXmp, xmp)
        }
        demand(code in 192..254 && code !in 208..216, Reason.INTEGRITY)
        val length = (input.byte() shl 8) + input.byte()
        demand(length >= 2, Reason.INTEGRITY)
        val block = input.block(length - 2)
        if (code in setOf(192, 193, 194)) {
            demand(block.size >= 6 && block.sliceArray(1..2).any { it != 0.toByte() } &&
                block.sliceArray(3..4).any { it != 0.toByte() }, Reason.INTEGRITY)
            frame = true
        }
        if (code == 218) {
            demand(frame && block.size >= 4, Reason.INTEGRITY)
            scan = true
            entropy = true
        }
        if (code == 225) {
            val text = block.toString(Charsets.UTF_8)
            demand(!text.startsWith("http://ns.adobe.com/xmp/extension/"), Reason.UNSUPPORTED)
            if (text.startsWith(XMP)) {
                demand(xmp == null, Reason.UNSUPPORTED)
                xmp = text.removePrefix(XMP)
                xmpAt = start
                afterXmp = input.position
            }
        }
    }
    error("JPEG marker loop ended")
}

private fun FileChannel.range(start: Long, end: Long): InputStream = object : InputStream() {
    private var position = start
    private val single = ByteArray(1)
    override fun read(): Int = if (read(single, 0, 1) == -1) -1 else single[0].toInt() and 255
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (len == 0) return 0
        if (position == end) return -1
        val count = this@range.read(ByteBuffer.wrap(b, off, minOf(end - position, len.toLong()).toInt()), position)
        demand(count > 0, Reason.INTEGRITY)
        position += count
        return count
    }
}

private fun embeddedLength(xmp: String): Pair<Long, Boolean>? {
    demand(!xmp.contains("<!"), Reason.UNSUPPORTED)
    val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
    val xml = try {
        factory.newDocumentBuilder().parse(xmp.byteInputStream())
    } catch (_: org.xml.sax.SAXException) {
        throw BridgeFailure(Reason.INTEGRITY)
    }
    val descriptions = xml.getElementsByTagNameNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description")
    val flags = (0 until descriptions.length).map { descriptions.item(it) as org.w3c.dom.Element }
    val motion = flags.filter { it.getAttributeNS(CAMERA, "MotionPhoto") == "1" }
    if (motion.isEmpty()) return null
    demand(motion.size == 1 && motion.single().getAttributeNS(CAMERA, "MotionPhotoVersion") == "1", Reason.UNSUPPORTED)
    val directories = xml.getElementsByTagNameNS(CONTAINER, "Directory")
    val entries = xml.getElementsByTagNameNS(CONTAINER, "Item")
    demand(directories.length == 1 && entries.length == 2, Reason.UNSUPPORTED)
    val primary = entries.item(0) as org.w3c.dom.Element
    val video = entries.item(1) as org.w3c.dom.Element
    demand(primary.getAttributeNS(ITEM, "Mime") == "image/jpeg" &&
        primary.getAttributeNS(ITEM, "Semantic") == "Primary" &&
        primary.getAttributeNS(ITEM, "Length") in setOf("", "0") &&
        primary.getAttributeNS(ITEM, "Padding") in setOf("", "0") &&
        video.getAttributeNS(ITEM, "Semantic") == "MotionPhoto" &&
        video.getAttributeNS(ITEM, "Mime") in setOf("video/mp4", "video/quicktime"), Reason.UNSUPPORTED)
    val length = video.getAttributeNS(ITEM, "Length").toLongOrNull()?.takeIf { it > 0 }
        ?: throw BridgeFailure(Reason.INTEGRITY)
    val rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    val elements = xml.getElementsByTagName("*")
    val allowed = mapOf("adobe:ns:meta/" to setOf("xmpmeta"), rdf to setOf("RDF", "Description", "Seq", "li"),
        CONTAINER to setOf("Directory", "Item"))
    val clean = (0 until elements.length).all { index ->
        val element = elements.item(index)
        element.localName in allowed[element.namespaceURI].orEmpty() &&
            (0 until element.attributes.length).all { attr ->
                val value = element.attributes.item(attr)
                when (value.namespaceURI) {
                    "http://www.w3.org/2000/xmlns/" -> true
                    rdf -> value.localName in setOf("about", "parseType")
                    CAMERA -> value.localName in setOf("MotionPhoto", "MotionPhotoVersion", "MotionPhotoPresentationTimestampUs")
                    ITEM -> value.localName in setOf("Mime", "Semantic", "Length", "Padding")
                    else -> false
                }
            }
    }
    return length to (clean && elements.length == 9)
}

fun validateMotionVideo(file: File) = file.inputStream().use {
    validateMotionVideo(it.channel, 0, it.channel.size())
}

private fun validateMotionVideo(channel: FileChannel, start: Long, end: Long) {
    demand(start >= 0 && end <= channel.size() && end - start >= 24, Reason.INTEGRITY)
    var position = start
    var count = 0
    val boxes = mutableSetOf<String>()
    while (position < end) {
        demand(++count <= 8192 && end - position >= 8, Reason.INTEGRITY)
        val source = DataInputStream(channel.range(position, end))
        val declared = source.readInt().toLong() and 0xffffffffL
        val kind = ByteArray(4).also(source::readFully).toString(Charsets.US_ASCII)
        val header = if (declared == 1L) 16 else 8
        demand(end - position >= header, Reason.INTEGRITY)
        val size = when (declared) {
            0L -> end - position
            1L -> source.readLong()
            else -> declared
        }
        demand(size >= header && size <= end - position, Reason.INTEGRITY)
        if (kind in setOf("moov", "mdat")) demand(size > header, Reason.INTEGRITY)
        if (count == 1) {
            demand(kind == "ftyp" && size >= header + 8, Reason.UNSUPPORTED)
            val brand = ByteArray(4).also(source::readFully).toString(Charsets.US_ASCII)
            demand(brand in setOf("qt  ", "isom", "iso2", "iso4", "iso5", "iso6", "mp41", "mp42", "avc1", "M4V ", "hvc1"), Reason.UNSUPPORTED)
        }
        if (kind in setOf("ftyp", "moov", "mdat")) demand(boxes.add(kind) || kind == "mdat", Reason.INTEGRITY)
        position += size
    }
    demand(boxes.containsAll(setOf("ftyp", "moov", "mdat")), Reason.INTEGRITY)
}

fun inspectMotionJpeg(file: File, check: () -> Unit = {}): MotionProof =
    file.inputStream().use { inspectMotionJpeg(it.channel, check) }

fun inspectMotionJpeg(channel: FileChannel, check: () -> Unit = {}): MotionProof {
    val total = channel.size()
    val layout = jpegLayout(channel, total, check)
    val embedded = layout.xmp?.let(::embeddedLength)
    if (embedded == null) {
        if (layout.end != total) {
            demand(layout.xmp == null, Reason.UNSUPPORTED)
            return inspectSefMotion(channel, total, layout.end, check)
        }
        val still = channel.range(0, total).use { digest(it, checkpoint = check) }
        return MotionProof(still, null, total, layout.xmp != null)
    }
    demand(total - layout.end == embedded.first, Reason.INTEGRITY)
    validateMotionVideo(channel, layout.end, total)
    val video = channel.range(layout.end, total).use { digest(it, checkpoint = check) }
    val still = SequenceInputStream(channel.range(0, layout.xmpAt), channel.range(layout.afterXmp, layout.end))
        .use { digest(it, checkpoint = check) }
    return MotionProof(still, video, total, true, embedded.second, layout.end)
}

// Conservative Samsung profile: one SEF MotionPhoto_Data entry covering the entire JPEG tail.
// Unknown SEF fields (including depth and auxiliary images) are left for review.
private fun inspectSefMotion(channel: FileChannel, total: Long, stillEnd: Long, check: () -> Unit): MotionProof {
    demand(total - stillEnd > 40, Reason.UNSUPPORTED)
    val footer = DataInputStream(channel.range(total - 8, total))
    fun DataInputStream.u32() = Integer.reverseBytes(readInt()).toLong() and 0xffffffffL
    val directorySize = footer.u32()
    demand(footer.readInt() == 0x53454654, Reason.UNSUPPORTED) // SEFT
    demand(directorySize == 24L, Reason.UNSUPPORTED)
    val directory = total - 8 - directorySize
    val index = DataInputStream(channel.range(directory, total - 8))
    demand(index.readInt() == 0x53454648 && index.u32() == 101L && index.u32() == 1L, Reason.UNSUPPORTED)
    val type = index.readInt()
    val distance = index.u32()
    val length = index.u32()
    demand(type == 0x0000300a && directory - distance == stillEnd && length == distance,
        Reason.INTEGRITY)
    val record = DataInputStream(channel.range(stillEnd, directory))
    demand(record.readInt() == type, Reason.INTEGRITY)
    val name = "MotionPhoto_Data"
    demand(record.u32() == name.length.toLong(), Reason.INTEGRITY)
    val actual = ByteArray(name.length).also(record::readFully)
    demand(actual.contentEquals(name.toByteArray(Charsets.US_ASCII)), Reason.INTEGRITY)
    val videoStart = stillEnd + 8 + name.length
    validateMotionVideo(channel, videoStart, directory)
    val still = channel.range(0, stillEnd).use { digest(it, checkpoint = check) }
    val video = channel.range(videoStart, directory).use { digest(it, checkpoint = check) }
    return MotionProof(still, video, total, false, true, videoStart)
}
