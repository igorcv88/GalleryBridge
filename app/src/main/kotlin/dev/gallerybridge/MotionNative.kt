package dev.gallerybridge

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import org.json.JSONObject

internal data class MotionLocal(
    val uri: Uri, val sha: String, val bytes: Long, val name: String, val path: String,
    val date: Long?, val favorite: Boolean?, val trashed: Boolean, val pending: Boolean,
    val generation: Long, val owner: String?
) {
    fun fingerprint(): String = motionDigest(uri.toString(), sha, bytes.toString(), name, path,
        date.toString(), favorite.toString(), trashed.toString(), generation.toString(), owner.orEmpty())
    fun journal(): String = JSONObject().put("uri", uri.toString()).put("sha", sha)
        .put("bytes", bytes).put("path", path).put("name", name)
        .put("date", date).put("fingerprint", fingerprint()).toString()
}

internal data class MotionOperation(
    val action: String, val digest: String, val survivor: String, val candidate: String, val targetPath: String?,
    val state: String
)

internal data class MotionAttestation(val playback: Boolean, val fields: Boolean, val localOnly: Boolean) {
    fun requireComplete() = demand(playback && fields && localOnly, Reason.DESTINATION_REVIEW)
}

internal data class MotionComparison(
    val survivor: MotionLocal, val candidate: MotionLocal, val decision: MotionDecision,
    val photo: Item?, val video: Item?, val sameCaptureFields: Boolean, val reason: String
) {
    val key = motionDigest("review-v1", survivor.fingerprint(), candidate.fingerprint(), decision.name)
    val eligible = sameCaptureFields && photo != null && video != null && decision in setOf(
        MotionDecision.KEEP_HISTORICAL, MotionDecision.KEEP_RECOMPOSED, MotionDecision.REVIEW_STATIC,
        MotionDecision.REVIEW_VIDEO
    )
}

internal class MotionNative(private val context: Context, private val ledger: Ledger, private val export: MotionExport) {
    private val resolver = context.contentResolver

    fun canonical(uri: Uri): Uri {
        val media = if (isMediaItem(uri)) uri else MediaStore.getMediaUri(context, uri)
        demand(media != null && isMediaItem(media), Reason.UNSUPPORTED)
        val local = requireNotNull(media)
        if (local.pathSegments.first() != MediaStore.VOLUME_EXTERNAL) return local
        val volume = resolver.query(local, arrayOf(MediaStore.MediaColumns.VOLUME_NAME), null, null, null)
            ?.use { if (it.moveToFirst()) it.getString(0) else null }
            ?: throw BridgeFailure(Reason.UNSUPPORTED)
        val collection = if (local.pathSegments[1] == "images") MediaStore.Images.Media.getContentUri(volume)
            else MediaStore.Video.Media.getContentUri(volume)
        return ContentUris.withAppendedId(collection, ContentUris.parseId(local))
    }

    private val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    private val publicationPath = "Pictures/GalleryBridge_Motion/"

    fun publish(photo: Item, reserve: Long, check: () -> Unit): Uri {
        val output = export.stage(photo, reserve, check)
        val token = "GB_Motion_${output.key}.jpg"
        val existing = ledger.motionPublication(output.key)
        val recovered = findOwned(token)
        if (existing?.first != null) demand(recovered?.toString() == existing.first, Reason.DESTINATION_REVIEW)
        if (existing?.second == "INDEXED_LOCAL") {
            val verified = requireNotNull(recovered)
            demand(snapshot(verified, false, check).sha == output.digest.sha, Reason.INTEGRITY)
            return verified
        }

        ledger.saveMotionPublication(output.key, output.digest.sha, recovered?.toString(), "STAGED")
        check()
        val uri = recovered ?: resolver.insert(imageCollection, values(
            MediaStore.MediaColumns.DISPLAY_NAME to token, MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
            MediaStore.MediaColumns.RELATIVE_PATH to publicationPath, MediaStore.MediaColumns.IS_PENDING to 1,
            MediaStore.MediaColumns.DATE_TAKEN to publicationDate(photo, ledger.getSidecar(photo.sha))
        )) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        ledger.saveMotionPublication(output.key, output.digest.sha, uri.toString(), "STAGED")
        val row = read(uri, true)
        demand(row.owner == context.packageName && row.path == publicationPath && row.name == token &&
            !row.trashed, Reason.DESTINATION_REVIEW)
        if (row.pending) {
            val sink = resolver.openOutputStream(uri, "wt") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
            sink.use { outputStream ->
                output.file.inputStream().use {
                    demand(digest(it, outputStream, output.digest.bytes, check) == output.digest, Reason.INTEGRITY)
                }
            }
            val stagedDigest = resolver.openInputStream(uri)?.use {
                digest(it, maxBytes = output.digest.bytes, checkpoint = check)
            }
            demand(stagedDigest == output.digest, Reason.INTEGRITY)
            check()
            demand(resolver.update(uri, values(MediaStore.MediaColumns.IS_PENDING to 0), null, null) == 1,
                Reason.NEEDS_USER_ACTION)
        }
        val copied = snapshot(uri, false, check)
        demand(copied.sha == output.digest.sha && copied.bytes == output.digest.bytes, Reason.INTEGRITY)
        ledger.saveMotionPublication(output.key, output.digest.sha, uri.toString(), "INDEXED_LOCAL")
        return uri
    }

    @Suppress("DEPRECATION")
    private fun selection(uri: Uri): Uri = if (Build.VERSION.SDK_INT == 29) MediaStore.setIncludePending(uri) else uri

    private fun inclusion(): Bundle = Bundle().apply {
        if (Build.VERSION.SDK_INT >= 30) {
            putInt(MediaStore.QUERY_ARG_MATCH_PENDING, MediaStore.MATCH_INCLUDE)
            putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_INCLUDE)
        }
    }

    private fun findOwned(name: String): Uri? {
        val args = inclusion().apply {
            putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION,
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?")
            putStringArray(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(name, publicationPath))
        }
        return resolver.query(selection(imageCollection), arrayOf(MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME), args, null)?.use { rows ->
            if (!rows.moveToFirst()) return@use null
            val result = ContentUris.withAppendedId(imageCollection, rows.getLong(0))
            demand(rows.getString(1) == context.packageName && !rows.moveToNext(), Reason.DESTINATION_REVIEW)
            result
        }
    }

    private fun read(uri: Uri, includeHidden: Boolean): MotionLocal {
        val names = mutableListOf(MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.IS_PENDING, MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
        if (Build.VERSION.SDK_INT >= 30) names += listOf(MediaStore.MediaColumns.IS_FAVORITE,
            MediaStore.MediaColumns.IS_TRASHED, MediaStore.MediaColumns.GENERATION_MODIFIED)
        return resolver.query(if (includeHidden) selection(uri) else uri, names.toTypedArray(),
            if (includeHidden) inclusion() else null, null)?.use {
            demand(it.moveToFirst(), Reason.NEEDS_USER_ACTION)
            val modern = Build.VERSION.SDK_INT >= 30
            val result = MotionLocal(uri, "", it.getLong(0), it.getString(1), it.getString(2).orEmpty(),
                if (it.isNull(3)) null else it.getLong(3), if (modern && !it.isNull(6)) it.getInt(6) != 0 else null,
                modern && it.getInt(7) != 0, it.getInt(4) != 0, if (modern) it.getLong(8) else 0, it.getString(5))
            demand(!it.moveToNext(), Reason.DESTINATION_REVIEW)
            result
        } ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
    }

    fun snapshot(uri: Uri, trashed: Boolean, check: () -> Unit): MotionLocal {
        val before = read(uri, trashed)
        demand(!before.pending && before.trashed == trashed && before.bytes in 1..MAX_MOTION_BYTES, Reason.DESTINATION_REVIEW)
        val stream = resolver.openInputStream(uri) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        val identity = stream.use { input ->
            digest(input, maxBytes = before.bytes, checkpoint = check)
        }
        demand(identity.bytes == before.bytes && read(uri, trashed) == before, Reason.SOURCE_CHANGED)
        return before.copy(sha = identity.sha)
    }

    fun matchingOriginal(survivor: Uri, check: () -> Unit): Item? {
        val proof = inspect(survivor, check).second ?: return null
        val embedded = proof.video ?: return null
        val photo = ledger.item(proof.still.sha)
        val video = photo?.let(export::pair)
        return photo.takeIf { video?.sha == embedded.sha && video.size == embedded.bytes }
    }

    fun compare(survivor: Uri, candidate: Uri, check: () -> Unit): MotionComparison {
        demand(survivor != candidate, Reason.DESTINATION_REVIEW)
        val a = inspect(survivor, check)
        val b = inspect(candidate, check)
        demand(a.first.uri.pathSegments.first() != b.first.uri.pathSegments.first() ||
            a.first.path != b.first.path || a.first.name != b.first.name, Reason.DESTINATION_REVIEW)
        val proof = a.second ?: throw BridgeFailure(Reason.UNSUPPORTED)
        val embedded = proof.video ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        val photo = ledger.item(proof.still.sha)
        val component = photo?.let(export::pair)?.takeIf { it.sha == embedded.sha }
        val survivorRole = if (ledger.isRecomposed(survivor.toString())) MotionRole.RECOMPOSED_MOTION else MotionRole.HISTORICAL_MOTION
        val candidateRole = when {
            b.second?.video != null && ledger.isRecomposed(candidate.toString()) -> MotionRole.RECOMPOSED_MOTION
            b.second?.video != null -> MotionRole.HISTORICAL_MOTION
            b.second != null -> MotionRole.STILL
            component?.sha == b.first.sha && !ledger.approvedVideo(b.first.sha) -> MotionRole.VIDEO_COMPONENT
            else -> MotionRole.STANDALONE_VIDEO
        }
        val fields = a.first.favorite != null && b.first.favorite != null &&
            (a.first.favorite == true || b.first.favorite == false) && a.first.date == b.first.date
        // Reviewed album membership and remote observations are handled separately by the UI.
        val decision = reconcileMotion(
            MotionCandidate(survivor.toString(), a.first.sha, proof.still.sha, embedded.sha, survivorRole),
            MotionCandidate(candidate.toString(), b.first.sha, b.second?.still?.sha, b.second?.video?.sha, candidateRole),
            fields, candidateRole == MotionRole.VIDEO_COMPONENT
        )
        val reason = when {
            !fields -> "Data ou favorito diverge, ou a informação não pôde ser lida."
            component == null -> "O par original com os dois componentes disponíveis no ZIP não foi confirmado."
            decision == MotionDecision.KEEP_BOTH -> "O vídeo é independente ou o movimento é diferente."
            decision == MotionDecision.HOLD -> "Os bytes não confirmam equivalência neste grupo."
            else -> "Correspondência de bytes local; associação e exclusão remotas não verificadas."
        }
        return MotionComparison(a.first, b.first, decision, photo, component, fields, reason)
    }

    private fun inspect(uri: Uri, check: () -> Unit): Pair<MotionLocal, MotionProof?> {
        val before = read(uri, false)
        if (uri.pathSegments[1] == "video") return snapshot(uri, false, check) to null
        demand(!before.pending && !before.trashed && before.bytes in 1..MAX_MOTION_BYTES, Reason.DESTINATION_REVIEW)
        val descriptor = resolver.openFileDescriptor(uri, "r") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        return android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { input ->
            demand(input.channel.size() == before.bytes, Reason.UNSUPPORTED)
            val proof = inspectMotionJpeg(input.channel, check)
            // Arbitrary historic XMP may carry protected metadata in the segment removed for comparison.
            demand(proof.video == null || proof.motionOnlyXmp, Reason.UNSUPPORTED)
            proof.video?.let {
                requireReadableStill(input)
                requireVideoTrack(input.fd, requireNotNull(proof.videoStart), it.bytes)
            }
            input.channel.position(0)
            val identity = digest(input, maxBytes = before.bytes, checkpoint = check)
            demand(identity.bytes == before.bytes && read(uri, false) == before, Reason.SOURCE_CHANGED)
            before.copy(sha = identity.sha) to proof
        }
    }

    private fun recheck(plan: MotionComparison, reserve: Long, check: () -> Unit) {
        demand(ledger.motionOperation()?.state !in setOf("TRASH_REQUESTED", "RESTORE_REQUESTED", "MOVE_REQUESTED"),
            Reason.DESTINATION_REVIEW)
        demand(plan.eligible && !ledger.protectedMotionSurvivor(plan.candidate.uri.toString(), plan.candidate.sha),
            Reason.DESTINATION_REVIEW)
        export.verifyArchives(requireNotNull(plan.photo), requireNotNull(plan.video), reserve, check)
        demand(snapshot(plan.survivor.uri, false, check) == plan.survivor &&
            snapshot(plan.candidate.uri, false, check) == plan.candidate, Reason.SOURCE_CHANGED)
        check()
    }

    fun moveForReview(plan: MotionComparison, attestation: MotionAttestation, reserve: Long, check: () -> Unit): MotionOperation {
        attestation.requireComplete()
        recheck(plan, reserve, check)
        demand(plan.candidate.owner == context.packageName &&
            plan.candidate.uri.pathSegments.first() == MediaStore.VOLUME_EXTERNAL_PRIMARY, Reason.UNSUPPORTED)
        val path = if (plan.candidate.uri.pathSegments[1] == "video") "Movies/GalleryBridge_Revisao/" else
            "Pictures/GalleryBridge_Revisao/"
        demand(plan.candidate.path != path, Reason.DESTINATION_REVIEW)
        requireFreeDestination(plan.candidate, path)
        val op = MotionOperation("MOVE", motionDigest(plan.key, "MOVE", "reviewed-local-only"),
            plan.survivor.journal(), plan.candidate.journal(), path, "MOVE_REQUESTED")
        return applyMove(op, plan.candidate.uri, path, check)
    }

    fun undoMove(check: () -> Unit): MotionOperation {
        val op = ledger.motionOperation() ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        demand(op.action == "MOVE" && op.state == "MOVED_LOCAL", Reason.DESTINATION_REVIEW)
        val original = JSONObject(op.candidate)
        val item = readJournal(op.candidate, false, check)
        readJournal(op.survivor, false, check)
        demand(item.owner == context.packageName && item.path == op.targetPath &&
            item.name == original.getString("name"), Reason.DESTINATION_REVIEW)
        val originalPath = original.getString("path")
        requireFreeDestination(item, originalPath)
        val rollback = MotionOperation("UNDO_MOVE", motionDigest(op.digest, "UNDO_MOVE"),
            op.survivor, item.journal(), originalPath, "MOVE_REQUESTED")
        return applyMove(rollback, item.uri, originalPath, check)
    }

    fun requireFreeDestination(item: MotionLocal, path: String) {
        val collection = item.uri.buildUpon().path(item.uri.path.orEmpty().substringBeforeLast('/')).build()
        val args = inclusion().apply {
            putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION,
                "${MediaStore.MediaColumns.RELATIVE_PATH}=?")
            putStringArray(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(path))
        }
        val free = resolver.query(collection, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), args, null)?.use { rows ->
            var conflict = false
            while (!conflict && rows.moveToNext()) conflict = rows.getString(0).equals(item.name, ignoreCase = true)
            !conflict
        }
        demand(free == true, Reason.DESTINATION_REVIEW)
    }

    @TargetApi(30)
    fun moveReviewedImage(item: MotionLocal, path: String): Int {
        val where = "${MediaStore.MediaColumns.RELATIVE_PATH}=? AND ${MediaStore.MediaColumns.DISPLAY_NAME}=? AND " +
            "${MediaStore.MediaColumns.SIZE}=? AND ${MediaStore.MediaColumns.GENERATION_MODIFIED}=? AND " +
            "${MediaStore.MediaColumns.IS_PENDING}=0 AND ${MediaStore.MediaColumns.IS_TRASHED}=0 AND " +
            "${MediaStore.MediaColumns.IS_FAVORITE}=0"
        return resolver.update(item.uri, values(MediaStore.MediaColumns.RELATIVE_PATH to path), where,
            arrayOf(item.path, item.name, item.bytes.toString(), item.generation.toString()))
    }

    private fun applyMove(op: MotionOperation, uri: Uri, path: String, check: () -> Unit): MotionOperation {
        check()
        ledger.saveMotionOperation(op)
        demand(resolver.update(uri, values(MediaStore.MediaColumns.RELATIVE_PATH to path),
            null, null) == 1, Reason.NEEDS_USER_ACTION)
        return finish(null, check) ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
    }

    fun requestTrash(plan: MotionComparison, attestation: MotionAttestation, reserve: Long, check: () -> Unit): PendingIntent {
        attestation.requireComplete()
        demand(Build.VERSION.SDK_INT >= 30, Reason.UNSUPPORTED)
        recheck(plan, reserve, check)
        val request = trashRequest(plan.candidate.uri, true)
        ledger.saveMotionOperation(MotionOperation("TRASH", motionDigest(plan.key, "TRASH", "reviewed-local-only"), plan.survivor.journal(),
            plan.candidate.journal(), null, "TRASH_REQUESTED"))
        return request
    }

    fun requestRestore(check: () -> Unit): PendingIntent {
        demand(Build.VERSION.SDK_INT >= 30, Reason.UNSUPPORTED)
        val op = ledger.motionOperation() ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        demand(op.state == "REMOVAL_OBSERVED", Reason.DESTINATION_REVIEW)
        val candidate = readJournal(op.candidate, true, check)
        val request = trashRequest(candidate.uri, false)
        ledger.saveMotionOperation(op.copy(action = "RESTORE", state = "RESTORE_REQUESTED"))
        return request
    }

    @TargetApi(30)
    private fun trashRequest(uri: Uri, value: Boolean): PendingIntent =
        MediaStore.createTrashRequest(resolver, listOf(uri), value)

    private fun readJournal(record: String, trashed: Boolean, check: () -> Unit): MotionLocal {
        val json = JSONObject(record)
        val local = snapshot(Uri.parse(json.getString("uri")), trashed, check)
        demand(local.sha == json.getString("sha") && local.bytes == json.getLong("bytes"), Reason.SOURCE_CHANGED)
        return local
    }

    fun finish(approved: Boolean?, check: () -> Unit): MotionOperation? {
        val op = ledger.motionOperation() ?: return null
        if (!op.state.endsWith("_REQUESTED")) return op
        val state = when {
            approved == false && op.action == "RESTORE" -> "REMOVAL_OBSERVED"
            approved == false -> "CANCELLED"
            else -> observedState(op, check)
        }
        return op.copy(state = state).also(ledger::saveMotionOperation)
    }

    private fun observedState(op: MotionOperation, check: () -> Unit): String {
        val original = JSONObject(op.candidate)
        val observation = try {
            readJournal(op.candidate, op.action == "TRASH", check)
        } catch (failure: BridgeFailure) {
            if (failure.reason in setOf(Reason.PAUSED, Reason.THERMAL)) throw failure
            if (op.action == "TRASH") {
                val retained = try { readJournal(op.candidate, false, check) } catch (_: java.io.IOException) { null }
                if (retained != null && retained.path == original.getString("path")) return "NOT_OBSERVED"
            }
            return op.state
        } catch (_: SecurityException) {
            return op.state
        } catch (_: java.io.IOException) {
            return op.state
        }
        if (op.action == "RESTORE") return if (observation.path == original.getString("path") &&
            observation.name == original.getString("name")) "RESTORED_LOCAL" else "HOLD"
        readJournal(op.survivor, false, check)
        return when {
            op.action == "TRASH" -> "REMOVAL_OBSERVED"
            observation.path == op.targetPath && observation.name == original.getString("name") ->
                if (op.action == "UNDO_MOVE") "RESTORED_PATH" else "MOVED_LOCAL"
            else -> "HOLD"
        }
    }
}
