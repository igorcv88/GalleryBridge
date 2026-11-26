package dev.gallerybridge

import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.json.JSONObject

internal data class PreparationEdit(val field: Field, val value: String)

internal fun suggestedPreparation(
    item: Item, sidecar: Sidecar?, albums: List<String>, field: Field, action: Action
): PreparationEdit? {
    val metadata = item.metadata?.let(::JSONObject)
    val embedded = metadata?.optJSONObject("exif")
    val keys = metadata?.optJSONObject("keys")
    val container = metadata?.optJSONObject("container")
    val raw = when (field) {
        Field.CAPTURE_TIME -> when (action) {
            Action.USE_TAKEOUT -> sidecar?.taken?.instant?.takeIf {
                val existing = embedded?.epoch()
                val embeddedUnknownOffset = embedded?.optString("basis") == "LOCAL_WALL_TIME"
                !embeddedUnknownOffset && (existing == null || kotlin.math.abs(existing - it.toEpochMilli()) > 2_000)
            }?.toString()
            Action.USE_FILE_METADATA -> embedded?.let(::dateWithOffset)
                ?: keys?.epoch()?.takeIf {
                    val other = container?.epoch()
                    other != null && kotlin.math.abs(other - it) > 2_000
                }?.let { dateWithOffset(keys) }
            else -> null
        }
        Field.LOCATION -> when (action) {
            Action.USE_TAKEOUT -> sidecar?.locations?.map { it.lat to it.lng }?.distinct()?.singleOrNull()
                ?.let { "${it.first},${it.second}" }
            Action.USE_FILE_METADATA -> metadata?.takeIf { it.has("latitude") && it.has("longitude") }
                ?.let { "${it.optDouble("latitude")},${it.optDouble("longitude")}" }
            else -> null
        }
        Field.DESCRIPTION -> when (action) {
            Action.USE_TAKEOUT -> sidecar?.description
            Action.USE_FILE_METADATA -> metadata?.optString("description")
            else -> null
        }
        Field.ALBUM -> if (action == Action.USE_TAKEOUT) albums.map { it.substringAfterLast('/') }
            .filter { it.isNotBlank() }.distinct().singleOrNull() else null
        Field.ORIENTATION -> if (action == Action.USE_FILE_METADATA) metadata?.optString("orientation") else null
        else -> null
    }
    return raw?.let { validatedPreparation(item, field, it) }
}

internal fun validatedPreparation(item: Item, field: Field, value: String): PreparationEdit? {
    if (item.state != "NEW") return null
    val normalized = when (field) {
        Field.CAPTURE_TIME -> {
            if (item.mime !in editableImages && !item.mime.startsWith("video/")) return null
            runCatching {
                OffsetDateTime.parse(value.trim()).also { require(it.year in 1900..9999) }.toString()
            }.getOrNull()
        }
        Field.LOCATION -> coordinates(value)?.takeIf { item.mime in editableImages }?.let { "${it.first},${it.second}" }
        Field.DESCRIPTION -> value.takeIf { item.mime in editableImages && it.length in 1..2048 && '\u0000' !in it }
        Field.ALBUM -> value.trim().takeIf { it.length in 1..70 && !it.startsWith('.') &&
            it.none { character -> character in "/\\:" || character.isISOControl() } }
        Field.ORIENTATION -> value.toIntOrNull()?.takeIf { item.mime in editableImages && it in 1..8 }?.toString()
        else -> null
    }
    return normalized?.let { PreparationEdit(field, it) }
}

private fun dateWithOffset(value: JSONObject): String? {
    val raw = value.optString("raw").trim().replaceFirst(
        Regex("^(\\d{4}):(\\d{2}):(\\d{2}) "), "$1-$2-$3T"
    ).replace(Regex(" +([+-]\\d{2}:?\\d{2})$"), "$1").replace(' ', 'T')
        .replace(Regex("([+-]\\d{2})(\\d{2})$"), "$1:$2")
    return raw.takeIf { value.epoch() != null }
}

private val editableImages = setOf("image/jpeg", "image/png", "image/webp")

private fun coordinates(value: String): Pair<Double, Double>? {
    val parts = value.split(',')
    if (parts.size != 2) return null
    val lat = parts[0].trim().toDoubleOrNull() ?: return null
    val lon = parts[1].trim().toDoubleOrNull() ?: return null
    return if (lat in -90.0..90.0 && lon in -180.0..180.0) lat to lon else null
}

internal fun writeAndVerifyExif(file: File, edits: List<PreparationEdit>) {
    val exif = ExifInterface(file)
    // EXIF transformations of a composite image require a dedicated motion-aware writer.
    demand(exif.getAttribute(ExifInterface.TAG_XMP)?.contains("MotionPhoto") != true &&
        exif.getAttribute(ExifInterface.TAG_XMP)?.contains("MicroVideo") != true, Reason.DESTINATION_REVIEW)
    if (file.extension == "jpg" || file.extension == "jpeg") {
        java.io.RandomAccessFile(file, "r").use { input ->
            demand(input.length() >= 2, Reason.INTEGRITY)
            input.seek(input.length() - 2)
            demand(input.readUnsignedShort() == 0xffd9, Reason.DESTINATION_REVIEW)
        }
    }
    val before = imageBounds(file)
    val expected = mutableMapOf<String, String>()
    var location: Pair<Double, Double>? = null
    for ((field, value) in edits) when (field) {
        Field.CAPTURE_TIME -> {
            val time = OffsetDateTime.parse(value)
            expected[ExifInterface.TAG_DATETIME_ORIGINAL] = time.format(DateTimeFormatter.ofPattern("uuuu:MM:dd HH:mm:ss", Locale.ROOT))
            expected[ExifInterface.TAG_OFFSET_TIME_ORIGINAL] = if (time.offset == ZoneOffset.UTC) "+00:00" else time.offset.id
        }
        Field.LOCATION -> location = requireNotNull(coordinates(value))
        Field.DESCRIPTION -> expected[ExifInterface.TAG_IMAGE_DESCRIPTION] = value
        Field.ORIENTATION -> expected[ExifInterface.TAG_ORIENTATION] = value
        else -> Unit
    }
    expected.forEach(exif::setAttribute)
    location?.let { exif.setLatLong(it.first, it.second) }
    exif.saveAttributes()
    val result = ExifInterface(file)
    demand(expected.all { (tag, value) -> result.getAttribute(tag) == value }, Reason.INTEGRITY)
    location?.let { desired ->
        val found = result.latLong
        demand(found != null && kotlin.math.abs(found[0] - desired.first) < 0.00001 &&
            kotlin.math.abs(found[1] - desired.second) < 0.00001, Reason.INTEGRITY)
    }
    demand(imageBounds(file) == before, Reason.INTEGRITY)
}

private fun imageBounds(file: File): Pair<Int, Int> {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, options)
    demand(options.outWidth > 0 && options.outHeight > 0, Reason.INTEGRITY)
    return options.outWidth to options.outHeight
}
