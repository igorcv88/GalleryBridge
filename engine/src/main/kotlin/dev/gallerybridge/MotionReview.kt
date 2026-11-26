package dev.gallerybridge

enum class MotionDecision { COMPOSE, KEEP_HISTORICAL, KEEP_RECOMPOSED, REVIEW_STATIC, REVIEW_VIDEO, HOLD, KEEP_BOTH }
enum class MotionRole { STILL, VIDEO_COMPONENT, HISTORICAL_MOTION, RECOMPOSED_MOTION, STANDALONE_VIDEO }

data class MotionCandidate(
    val id: String, val sha: String, val stillSha: String?, val videoSha: String?, val role: MotionRole
)

fun reconcileMotion(
    survivor: MotionCandidate, candidate: MotionCandidate, protectedFieldsMatch: Boolean,
    confirmedComponent: Boolean, historicalHintsOnly: Boolean = false
): MotionDecision {
    if (historicalHintsOnly || survivor.id == candidate.id || survivor.videoSha == null ||
        !protectedFieldsMatch) return MotionDecision.HOLD
    return when (candidate.role) {
        MotionRole.STANDALONE_VIDEO -> MotionDecision.KEEP_BOTH
        MotionRole.VIDEO_COMPONENT ->
            if (confirmedComponent && candidate.sha == survivor.videoSha) MotionDecision.REVIEW_VIDEO else MotionDecision.HOLD
        MotionRole.STILL ->
            if (candidate.sha == survivor.stillSha) MotionDecision.REVIEW_STATIC else MotionDecision.HOLD
        MotionRole.HISTORICAL_MOTION, MotionRole.RECOMPOSED_MOTION -> when {
            candidate.stillSha != survivor.stillSha || candidate.stillSha == null -> MotionDecision.HOLD
            candidate.videoSha != survivor.videoSha -> MotionDecision.KEEP_BOTH
            survivor.role == MotionRole.HISTORICAL_MOTION -> MotionDecision.KEEP_HISTORICAL
            else -> MotionDecision.KEEP_RECOMPOSED
        }
    }
}

fun motionDigest(vararg fields: String): String = motionDigest(fields.asList())

fun motionDigest(fields: List<String>): String =
    digest(fields.joinToString("") { "${it.length}:$it" }.byteInputStream()).sha
