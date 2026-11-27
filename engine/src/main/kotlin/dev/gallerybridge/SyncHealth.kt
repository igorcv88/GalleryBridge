package dev.gallerybridge

enum class SyncCycle { ACTIVE, PAUSED_TRANSIENT, COMPLETED, UNKNOWN }
enum class SyncHealth {
    UNKNOWN, ACTIVE_DRAINING, BACKOFF_SCHEDULED, TRANSIENT_PAUSE, STALLED_RETRY,
    RECONCILING, COMPLETED_OBSERVED, LAST_SUCCESS_ADVANCED
}

data class SyncRetry(val id: Long, val entity: Long, val generation: Long, val retries: Long)
data class SyncSample(
    val observedAt: Long, val commits: Long, val future: Long, val dirty: Long,
    val lastSuccess: Long?, val cycle: SyncCycle = SyncCycle.UNKNOWN,
    val cycleAt: Long? = null, val completeRetrySet: List<SyncRetry>? = null
)

data class SyncAssessment(val health: SyncHealth, val commitsPerMinute: Double? = null)

fun assessSync(previous: SyncSample?, current: SyncSample): SyncAssessment {
    val span = previous?.let { current.observedAt - it.observedAt } ?: 0
    val fresh = current.cycleAt?.let { current.observedAt - it in 0..90_000 } == true
    val cycle = if (fresh) current.cycle else SyncCycle.UNKNOWN
    val drained = previous?.let { it.commits - current.commits } ?: 0
    if (cycle == SyncCycle.PAUSED_TRANSIENT) return SyncAssessment(SyncHealth.TRANSIENT_PAUSE)
    if (cycle == SyncCycle.COMPLETED) {
        val old = previous?.lastSuccess
        val now = current.lastSuccess
        val advanced = old != null && now != null && now > old &&
            now in (current.cycleAt!! - 60_000)..current.observedAt
        return SyncAssessment(if (advanced) SyncHealth.LAST_SUCCESS_ADVANCED else SyncHealth.COMPLETED_OBSERVED)
    }
    if (span >= 5_000 && drained > 0 && (cycle == SyncCycle.ACTIVE || current.commits > 0)) {
        return SyncAssessment(SyncHealth.ACTIVE_DRAINING, drained * 60_000.0 / span)
    }
    if (span >= 60_000 && previous != null && sameJobsRetried(previous, current)) {
        return SyncAssessment(SyncHealth.STALLED_RETRY)
    }
    return SyncAssessment(when {
        current.commits > 0 && current.future == current.commits -> SyncHealth.BACKOFF_SCHEDULED
        cycle == SyncCycle.ACTIVE || current.commits == 0L && current.dirty > 0 -> SyncHealth.RECONCILING
        else -> SyncHealth.UNKNOWN
    })
}

private fun sameJobsRetried(previous: SyncSample, current: SyncSample): Boolean {
    val old = previous.completeRetrySet ?: return false
    val now = current.completeRetrySet ?: return false
    if (old.isEmpty() || old.size.toLong() != previous.commits || now.size.toLong() != current.commits) return false
    fun key(retry: SyncRetry) = Triple(retry.id, retry.entity, retry.generation)
    val baseline = old.associateBy(::key)
    if (baseline.size != old.size || now.map(::key).distinct().size != now.size ||
        baseline.keys != now.map(::key).toSet()) return false
    return now.all { it.retries >= baseline.getValue(key(it)).retries } &&
        now.any { it.retries > baseline.getValue(key(it)).retries }
}
