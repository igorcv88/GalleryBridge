package dev.gallerybridge

fun verifiedFormat(header: ByteArray, mime: String): Boolean {
    fun starts(vararg signature: Int) =
        header.size >= signature.size && signature.indices.all { header[it].toInt() and 0xff == signature[it] }

    fun label(at: Int, expected: String): Boolean =
        header.size >= at + expected.length && expected.indices.all { header[at + it] == expected[it].code.toByte() }

    return when (mime) {
        "image/jpeg" -> starts(0xff, 0xd8, 0xff)
        "image/png" -> starts(0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a)
        "image/webp" -> label(0, "RIFF") && label(8, "WEBP")
        "image/gif" -> label(0, "GIF87a") || label(0, "GIF89a")
        "image/x-adobe-dng" -> starts(0x49, 0x49, 42, 0) || starts(0x4d, 0x4d, 0, 42)
        "image/heic", "image/heif" -> label(4, "ftyp") &&
            listOf("heic", "heix", "mif1", "msf1", "hevc", "hevx").any { label(8, it) }
        "image/avif" -> label(4, "ftyp") && (label(8, "avif") || label(8, "avis"))
        "video/mp4", "video/quicktime", "video/3gpp" ->
            listOf("ftyp", "moov", "mdat", "wide", "free").any { label(4, it) }
        "video/x-matroska", "video/webm" -> starts(0x1a, 0x45, 0xdf, 0xa3)
        else -> false
    }
}
