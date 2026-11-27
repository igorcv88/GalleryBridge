package dev.gallerybridge

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import org.json.JSONObject

enum class Field { CAPTURE_TIME, LOCATION, FAVORITE, DESCRIPTION, ALBUM, ORIENTATION, EDIT_VARIANT }
enum class Action { KEEP_DESTINATION, USE_TAKEOUT, USE_FILE_METADATA, NO_CHANGE, REVIEW, REPORT_ONLY, UNSUPPORTED }
enum class TimeBasis { OFFSET_AWARE, LOCAL_WALL_TIME, NOT_APPLICABLE, UNKNOWN }

data class TimeFact(val raw: String, val source: String, val instant: Instant?, val wall: LocalDateTime?) {
    val basis get() = when {
        instant != null -> TimeBasis.OFFSET_AWARE
        wall != null -> TimeBasis.LOCAL_WALL_TIME
        else -> TimeBasis.UNKNOWN
    }
}

fun cameraTime(raw: String, offset: String?, source: String): TimeFact {
    val iso = raw.trim().replaceFirst(Regex("^(\\d{4}):(\\d{2}):(\\d{2}) "), "$1-$2-$3T")
        .replace(' ', 'T')
        .replace(Regex("([+-]\\d{2})(\\d{2})$"), "$1:$2")
    val withOffset = if (offset.isNullOrBlank()) iso else iso + offset.trim()
    val instant = runCatching { OffsetDateTime.parse(withOffset).toInstant() }.getOrNull()
    val wall = runCatching { LocalDateTime.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }.getOrNull()
    return TimeFact(if (offset.isNullOrBlank()) raw else "$raw $offset", source, instant, wall)
}

fun epochTime(seconds: Long, source: String): TimeFact? = runCatching {
    val instant = Instant.ofEpochSecond(seconds)
    require(instant.atOffset(ZoneOffset.UTC).year in 1..9999)
    TimeFact(seconds.toString(), source, instant, null)
}.getOrNull()

enum class TimeConflict { NO_CONFLICT, CONTEXT_REQUIRED, DIFFERENT, MATERIAL, DIFFERENT_DAY_RANGE }

fun compareTime(candidate: TimeFact?, destination: TimeFact?): TimeConflict {
    val first = candidate?.instant ?: return TimeConflict.CONTEXT_REQUIRED
    val second = destination?.instant ?: return TimeConflict.CONTEXT_REQUIRED
    val delta = kotlin.math.abs(ChronoUnit.MILLIS.between(first, second))
    return when {
        delta <= 2_000 -> TimeConflict.NO_CONFLICT
        delta < 60_000 -> TimeConflict.DIFFERENT
        delta < 86_400_000 -> TimeConflict.MATERIAL
        else -> TimeConflict.DIFFERENT_DAY_RANGE
    }
}

data class Location(val lat: Double, val lng: Double, val source: String)

data class Sidecar(
    val title: String?, val taken: TimeFact?, val created: TimeFact?,
    val favorite: Boolean?, val description: String?, val locations: List<Location>,
    val favoriteObservations: Set<Boolean> = setOfNotNull(favorite)
)

fun parseSidecar(text: String): Sidecar? {
    val json = JSONObject(text)
    if (listOf("photoTakenTime", "creationTime", "geoData", "geoDataExif", "favorited", "isFavorite")
            .none { json.has(it) }) return null
    fun time(key: String) = json.optJSONObject(key)?.optString("timestamp")?.toLongOrNull()
        ?.let { epochTime(it, "Takeout:$key") }
    fun coordinates(key: String): Location? {
        val value = json.optJSONObject(key) ?: return null
        val lat = value.optDouble("latitude")
        val lng = value.optDouble("longitude")
        return if (lat in -90.0..90.0 && lng in -180.0..180.0 && (lat != 0.0 || lng != 0.0))
            Location(lat, lng, "Takeout:$key") else null
    }
    val favoriteFlags = listOfNotNull(json.opt("favorited") as? Boolean, json.opt("isFavorite") as? Boolean)
    return Sidecar(
        (json.opt("title") as? String)?.take(1024), time("photoTakenTime"), time("creationTime"),
        favoriteFlags.distinct().singleOrNull(), (json.opt("description") as? String)?.take(65_536),
        listOfNotNull(coordinates("geoData"), coordinates("geoDataExif")), favoriteFlags.toSet()
    )
}

data class MediaId(val sha: String, val video: Boolean, val internalId: String?)

fun livePair(item: MediaId, candidates: List<MediaId>): MediaId? {
    val id = normalizeLiveId(item.internalId) ?: return null
    val matching = candidates.filter { normalizeLiveId(it.internalId) == id }.distinctBy { it.sha }
    if (matching.none { it.sha == item.sha } || matching.count { it.video == item.video } != 1) return null
    return matching.singleOrNull { it.video != item.video }
}

fun normalizeLiveId(value: String?): String? = value?.trim()?.lowercase(Locale.ROOT)
    ?.takeIf { it.matches(Regex("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")) }
