package dev.gallerybridge

data class DuplicateFile(val id: String, val hash: String, val bytes: Long, val protected: Boolean, val capture: Long?)
data class DuplicateGroup(val keeper: DuplicateFile, val candidates: List<DuplicateFile>)

fun exactDuplicateGroups(files: List<DuplicateFile>): List<DuplicateGroup> {
    val distinct = files.groupBy { it.id }.values.mapNotNull { revisions -> revisions.distinct().singleOrNull() }
    return distinct.groupBy { it.hash to it.bytes }.values.mapNotNull { members ->
        if (members.size < 2) return@mapNotNull null
        // Preserve a protected copy when there is one, and never offer another protected copy as a target.
        val keeper = members.sortedWith(compareByDescending<DuplicateFile> { it.protected }.thenBy { it.id }).first()
        val candidates = members.filter { it.id != keeper.id && !it.protected && it.capture == keeper.capture }
        DuplicateGroup(keeper, candidates).takeIf { candidates.isNotEmpty() }
    }
}
