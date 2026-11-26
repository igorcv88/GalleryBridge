package dev.gallerybridge

enum class QueueScope { CURRENT_BATCH, VERIFIED_EXTERNAL, UNKNOWN, NONE }

data class QueueObservation(val entity: Long?, val path: String?, val kind: Int)
data class VerifiedQueueEntry(val entity: Long, val path: String, val kind: Int, val batch: Long)

fun scopeFor(
    currentBatch: Long, observation: QueueObservation, verified: List<VerifiedQueueEntry>
): QueueScope {
    val match = verified.singleOrNull {
        it.entity == observation.entity && it.path == observation.path && it.kind == observation.kind
    } ?: return QueueScope.UNKNOWN
    return if (match.batch == currentBatch) QueueScope.CURRENT_BATCH else QueueScope.VERIFIED_EXTERNAL
}

enum class LocalPresence { INDEXED_LOCAL, MISSING_WITH_SDK_LINK, LOCAL_ABSENCE_UNKNOWN }

fun localPresence(indexed: Boolean, exactCleanSdkLink: Boolean) = when {
    indexed -> LocalPresence.INDEXED_LOCAL
    exactCleanSdkLink -> LocalPresence.MISSING_WITH_SDK_LINK
    else -> LocalPresence.LOCAL_ABSENCE_UNKNOWN
}

data class Conditions(
    val wifi: Boolean, val minimumBattery: Int, val battery: Int?, val charging: Boolean,
    val onlyCharging: Boolean, val thermal: Int, val headroom: Float?, val paused: Boolean
)

fun blockReason(c: Conditions): Reason? = when {
    c.paused -> Reason.PAUSED
    c.thermal >= 3 || (c.headroom?.let { it >= 0.90f } == true) -> Reason.THERMAL
    !c.wifi -> Reason.WIFI_REQUIRED
    c.battery == null || c.battery < c.minimumBattery || (c.onlyCharging && !c.charging) -> Reason.BATTERY
    else -> null
}

fun freeUpAllowed(
    expected: Set<String>, exactCleanLinks: Set<String>, sourceBackupsVerified: Boolean,
    queue: List<QueueScope>, knownAccountAndAlbum: Boolean
): Boolean = expected.isNotEmpty() && expected == exactCleanLinks && sourceBackupsVerified &&
    knownAccountAndAlbum && queue.none { it == QueueScope.CURRENT_BATCH || it == QueueScope.UNKNOWN }
