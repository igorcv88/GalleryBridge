package dev.gallerybridge

import java.io.DataInputStream
import java.io.InputStream

data class QuickTimeFacts(val keys: TimeFact?, val container: TimeFact?, val contentId: String?)

private const val QUICKTIME_EPOCH = 2_082_844_800L
private const val MAX_METADATA = 2 * 1024 * 1024

fun inspectQuickTime(source: InputStream, size: Long): QuickTimeFacts {
    val input = DataInputStream(source)
    var keys: String? = null
    val ids = mutableSetOf<String>()
    var container: TimeFact? = null
    fun inspect(length: Long, depth: Int) {
        atoms(input, length) { name, bytes ->
            when {
                name == "moov" && depth == 0 -> inspect(bytes, 1)
                name == "udta" && depth == 1 -> inspect(bytes, 2)
                name == "mvhd" && depth == 1 && bytes in 12..128 -> {
                    val box = DataInputStream(input.readExact(bytes.toInt()).inputStream())
                    val epoch = when (box.readInt() ushr 24) {
                        0 -> box.readInt().toLong() and 0xffff_ffffL
                        1 -> box.readLong()
                        else -> 0
                    }
                    if (epoch > 0) container = epochTime(epoch - QUICKTIME_EPOCH, "QuickTime:CreateDate")
                }
                name == "meta" && depth in 1..2 && bytes in 8..MAX_METADATA.toLong() -> {
                    val data = input.readExact(bytes.toInt())
                    val tags = quickTimeTags(data, 0).ifEmpty { quickTimeTags(data, 4) }
                    tags["com.apple.quicktime.creationdate"]?.let { keys = it }
                    tags["com.apple.quicktime.content.identifier"]?.let { ids.add(it) }
                }
                else -> skip(input, bytes)
            }
        }
    }
    inspect(size, 0)
    return QuickTimeFacts(keys?.let { cameraTime(it, null, "Keys:CreationDate") }, container, ids.map { normalizeLiveId(it) }.distinct().singleOrNull())
}

private fun quickTimeTags(bytes: ByteArray, offset: Int): Map<String, String> = runCatching {
    val input = DataInputStream(bytes.inputStream(offset, bytes.size - offset))
    var names = emptyList<String>()
    var values = emptyMap<Int, String>()
    atoms(input, (bytes.size - offset).toLong()) { name, length ->
        when (name) {
            "keys" -> names = readKeys(input.readExact(length.toInt()))
            "ilst" -> values = readValues(input.readExact(length.toInt()))
            else -> skip(input, length)
        }
    }
    names.mapIndexedNotNull { index, key -> values[index + 1]?.let { key to it } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (key, entries) ->
            if (key == "com.apple.quicktime.content.identifier" && entries.distinct().size > 1) ""
            else entries.last()
        }
}.getOrDefault(emptyMap())

private fun readKeys(bytes: ByteArray): List<String> {
    val input = DataInputStream(bytes.inputStream())
    input.readInt()
    val count = input.readInt()
    require(count in 0..4096)
    return List(count) {
        val size = input.readInt()
        require(size in 8..minOf(4096, input.available() + 4))
        val namespace = input.readInt()
        val name = input.readExact(size - 8).toString(Charsets.UTF_8)
        if (namespace == 0x6d647461) name else ""
    }
}

private fun readValues(bytes: ByteArray): Map<Int, String> = buildMap {
    val input = DataInputStream(bytes.inputStream())
    atoms(input, bytes.size.toLong()) { key, length ->
        val index = key.fold(0) { acc, char -> (acc shl 8) or char.code }
        val item = DataInputStream(input.readExact(length.toInt()).inputStream())
        atoms(item, length) { type, size ->
            if (type == "data" && size in 9..4096) {
                val kind = item.readInt() and 0xff_ffff
                item.readInt()
                val value = item.readExact(size.toInt() - 8)
                if (kind == 1) put(index, value.toString(Charsets.UTF_8))
            } else skip(item, size)
        }
    }
}

private inline fun atoms(input: DataInputStream, length: Long, handle: (String, Long) -> Unit) {
    var left = length
    var count = 0
    while (left >= 8) {
        require(++count <= 100_000)
        val compact = input.readInt().toLong() and 0xffff_ffffL
        val type = ByteArray(4)
        input.readFully(type)
        val header = if (compact == 1L) 16L else 8L
        require(left >= header)
        val size = when (compact) { 0L -> left; 1L -> input.readLong(); else -> compact }
        require(size in header..left)
        handle(type.toString(Charsets.ISO_8859_1), size - header)
        left -= size
    }
    skip(input, left)
}

private fun skip(input: InputStream, amount: Long) {
    var remaining = amount
    while (remaining > 0) {
        val n = input.skip(remaining)
        if (n > 0) remaining -= n else {
            if (input.read() == -1) throw java.io.EOFException()
            remaining--
        }
    }
}

private fun DataInputStream.readExact(length: Int): ByteArray = ByteArray(length).also { readFully(it) }
