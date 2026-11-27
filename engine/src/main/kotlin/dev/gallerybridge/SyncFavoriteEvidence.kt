package dev.gallerybridge

import java.net.URI

enum class FavoriteObservationLevel { LOCAL_STATE, SDK_CACHE, REMOTE_VISUAL, REMOTE_INDEPENDENT }

enum class SdkFavorite { NO_RECORD, AMBIGUOUS, DIRTY, CLEAN_FAVORITE, CLEAN_NOT_FAVORITE, CLEAN_UNKNOWN }

fun classifySdkFavorite(rows: Long, clean: Long, positive: Long, negative: Long): SdkFavorite = when {
    rows == 0L -> SdkFavorite.NO_RECORD
    rows != 1L -> SdkFavorite.AMBIGUOUS
    clean != 1L -> SdkFavorite.DIRTY
    positive == 1L && negative == 0L -> SdkFavorite.CLEAN_FAVORITE
    negative == 1L && positive == 0L -> SdkFavorite.CLEAN_NOT_FAVORITE
    else -> SdkFavorite.CLEAN_UNKNOWN
}

private val photosItemPath = Regex("/photo/([A-Za-z0-9_-]{10,512})")

/** Return only the identity. Takeout links can contain account parameters and access tokens. */
fun googlePhotosItemKey(text: String): String? {
    val url = runCatching { URI(text) }.getOrNull() ?: return null
    if (url.scheme != "https" || !url.host.equals("photos.google.com", ignoreCase = true) ||
        url.rawUserInfo != null || url.port != -1) return null
    return photosItemPath.matchEntire(url.rawPath.orEmpty())?.groupValues?.get(1)
}
