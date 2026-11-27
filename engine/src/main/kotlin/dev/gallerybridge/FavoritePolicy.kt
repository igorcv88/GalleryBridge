package dev.gallerybridge

enum class HistoricalFavorite { FAVORITED, EXPLICITLY_NOT_FAVORITED, UNKNOWN, CONFLICT }

fun historicalFavorite(flags: Iterable<Boolean>): HistoricalFavorite {
    val observations = flags.toSet()
    return when {
        observations.size > 1 -> HistoricalFavorite.CONFLICT
        true in observations -> HistoricalFavorite.FAVORITED
        false in observations -> HistoricalFavorite.EXPLICITLY_NOT_FAVORITED
        else -> HistoricalFavorite.UNKNOWN
    }
}

enum class FavoriteState {
    SOURCE_CONFLICT, NOT_STAGED, NEEDS_NATIVE_RESTORE, AMBIGUOUS_DESTINATION, ACCESS_REQUIRED,
    READY, ALREADY_LOCAL, AWAITING_CONSENT, LOCAL_VERIFIED, NOT_APPLIED, PROVIDER_REJECTED
}
