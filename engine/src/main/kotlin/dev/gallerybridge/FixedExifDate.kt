package dev.gallerybridge

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val capturePattern = DateTimeFormatter.ofPattern("uuuu:MM:dd HH:mm:ss", Locale.ROOT)
private val tiffWidths = mapOf(1 to 1, 2 to 1, 3 to 2, 4 to 4, 5 to 8, 6 to 1, 7 to 1, 8 to 2, 9 to 4, 10 to 8)
private const val CAPTURE_TAG = 0x9003
private const val OFFSET_TAG = 0x9011
private const val SUBSEC_TAG = 0x9291

/** Edits only the fixed-width ASCII storage of existing capture tags in a plain verified JPEG. */
fun patchExifCapture(source: ByteArray, date: OffsetDateTime): ByteArray {
    demand(date.year in 1900..9999 && date.offset.totalSeconds % 60 == 0, Reason.DESTINATION_REVIEW)
    val layout = fixedExifLayout(source)
    val time = layout[CAPTURE_TAG] ?: throw BridgeFailure(Reason.UNSUPPORTED)
    val offset = layout[OFFSET_TAG] ?: throw BridgeFailure(Reason.UNSUPPORTED)
    val fractional = layout[SUBSEC_TAG]
    val result = source.copyOf()
    fun replace(field: IntRange, value: String) {
        val text = (value + "\u0000").toByteArray(Charsets.US_ASCII)
        demand(text.size == field.count() && source[field.last] == 0.toByte(), Reason.UNSUPPORTED)
        text.copyInto(result, field.first)
    }
    replace(time, date.format(capturePattern))
    replace(offset, if (date.offset.totalSeconds == 0) "+00:00" else date.offset.id)
    if (fractional == null) {
        demand(date.nano == 0, Reason.UNSUPPORTED)
    } else {
        val digits = fractional.count() - 1
        demand(digits in 1..9, Reason.UNSUPPORTED)
        val value = date.nano.toString().padStart(9, '0')
        demand(value.drop(digits).all { it == '0' }, Reason.UNSUPPORTED)
        replace(fractional, value.take(digits))
    }
    return result
}

private fun fixedExifLayout(source: ByteArray): Map<Int, IntRange> {
    fun byte(i: Int) = source[i].toInt() and 255
    demand(source.size >= 20 && byte(0) == 255 && byte(1) == 216 &&
        byte(source.lastIndex - 1) == 255 && byte(source.lastIndex) == 217, Reason.UNSUPPORTED)
    var i = 2
    var exifStart = -1
    var exifEnd = -1
    while (i + 4 <= source.size) {
        demand(byte(i) == 255, Reason.UNSUPPORTED)
        val marker = byte(i + 1)
        if (marker == 218) break
        val size = (byte(i + 2) shl 8) + byte(i + 3)
        demand(size >= 2 && size <= source.size - i - 2, Reason.UNSUPPORTED)
        if (marker == 225) {
            demand(size >= 8 && source.copyOfRange(i + 4, i + 10).contentEquals("Exif\u0000\u0000".toByteArray()) &&
                exifStart == -1, Reason.UNSUPPORTED)
            exifStart = i + 10
            exifEnd = i + 2 + size
        }
        if (marker == 226 && size >= 6) {
            // Reject MPF and all other APP2 profiles except the standard, untouched ICC profile block.
            val end = minOf(i + 16, i + 2 + size)
            demand(source.copyOfRange(i + 4, end).toString(Charsets.US_ASCII).startsWith("ICC_PROFILE\u0000"),
                Reason.UNSUPPORTED)
        }
        i += size + 2
    }
    demand(i + 4 <= source.size && byte(i + 1) == 218 && exifStart >= 0 && exifEnd - exifStart >= 8,
        Reason.UNSUPPORTED)
    return exifFields(source, exifStart, exifEnd)
}

private fun exifFields(source: ByteArray, start: Int, end: Int): Map<Int, IntRange> {
    val bytes = ByteBuffer.wrap(source, start, end - start).slice()
    val length = bytes.capacity()
    bytes.order(when (bytes.getShort(0).toInt() and 0xffff) {
        0x4949 -> ByteOrder.LITTLE_ENDIAN
        0x4d4d -> ByteOrder.BIG_ENDIAN
        else -> throw BridgeFailure(Reason.UNSUPPORTED)
    })
    fun u16(p: Int) = bytes.getShort(p).toInt() and 0xffff
    demand(u16(2) == 42, Reason.UNSUPPORTED)
    val occupied = mutableListOf(0..7)
    fun claim(range: IntRange) {
        demand(range.first >= 0 && range.last < length &&
            occupied.none { it.first <= range.last && range.first <= it.last }, Reason.UNSUPPORTED)
        occupied.add(range)
    }
    var exifOffset: Int? = null
    val fields = mutableMapOf<Int, IntRange>()
    fun parseDirectory(at: Int, exif: Boolean) {
        demand(at in 8..(length - 6), Reason.UNSUPPORTED)
        val count = u16(at)
        val endOfEntries = at + 2 + count * 12
        demand(count in 1..256 && endOfEntries <= length - 4, Reason.UNSUPPORTED)
        claim(at..(endOfEntries + 3))
        demand(bytes.getInt(endOfEntries) == 0, Reason.UNSUPPORTED) // no unverified thumbnail IFD
        val tags = mutableSetOf<Int>()
        repeat(count) { index ->
            val entry = at + 2 + index * 12
            val tag = u16(entry)
            val type = u16(entry + 2)
            val elements = bytes.getInt(entry + 4)
            val width = tiffWidths[type] ?: throw BridgeFailure(Reason.UNSUPPORTED)
            demand(tags.add(tag) && elements in 1..(length / width), Reason.UNSUPPORTED)
            val size = width * elements
            val raw = bytes.getInt(entry + 8)
            val offset = if (size <= 4) entry + 8 else raw
            demand(offset >= 0 && offset <= length - size, Reason.UNSUPPORTED)
            if (size > 4) claim(offset until offset + size)
            when (tag) {
                0x8769 -> {
                    demand(!exif && type == 4 && elements == 1, Reason.UNSUPPORTED)
                    exifOffset = raw
                }
                0x8825, 0x927c, 0xa005, 0x014a, 0x0111, 0x0201 -> throw BridgeFailure(Reason.UNSUPPORTED)
                CAPTURE_TAG, OFFSET_TAG, SUBSEC_TAG -> {
                    demand(exif && type == 2, Reason.UNSUPPORTED)
                    fields[tag] = (start + offset) until (start + offset + size)
                }
            }
        }
    }
    parseDirectory(bytes.getInt(4), false)
    parseDirectory(exifOffset ?: throw BridgeFailure(Reason.UNSUPPORTED), true)
    return fields
}
