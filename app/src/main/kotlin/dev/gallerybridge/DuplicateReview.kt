package dev.gallerybridge

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import org.json.JSONObject

internal const val DUPLICATE_DESTINATION = "Pictures/GalleryBridge_Revisao_Duplicatas/"

internal data class DuplicateJob(val uri: String, val plan: String, val keeper: String, val candidate: String, val state: String)
internal data class LocalDuplicateGroup(val keeper: MotionLocal, val candidates: List<MotionLocal>)
internal data class DuplicatePreview(val groups: List<LocalDuplicateGroup>, val held: Int) {
    val candidates = groups.flatMap { it.candidates }
    val digest = motionDigest(listOf("duplicates-v1", DUPLICATE_DESTINATION) + groups.flatMap { group ->
        listOf(group.keeper.fingerprint()) + group.candidates.map(MotionLocal::fingerprint)
    })
}

internal class DuplicateReview(private val context: Context, private val ledger: Ledger, private val media: MotionNative) {
    fun preview(documents: List<Uri>, check: () -> Unit): DuplicatePreview {
        demand(Build.VERSION.SDK_INT >= 30, Reason.UNSUPPORTED)
        demand(documents.size <= 1000, Reason.DESTINATION_REVIEW)
        var held = 0
        val available = linkedMapOf<String, MotionLocal>()
        val paths = mutableSetOf<String>()
        for (row in readCandidates(documents.distinct(), check)) {
            if (row == null || row.path == DUPLICATE_DESTINATION) {
                held++
                continue
            }
            val key = row.uri.toString()
            if (key in available) continue
            val path = row.uri.pathSegments.first() + ":" + row.path + row.name
            if (!paths.add(path)) { held++; continue }
            available[key] = row
        }
        val groups = exactDuplicateGroups(available.map { (key, row) ->
            DuplicateFile(key, row.sha, row.bytes,
                row.favorite != false || ledger.protectedMotionSurvivor(key, row.sha),
                row.date)
        }).map { group ->
            LocalDuplicateGroup(available.getValue(group.keeper.id), group.candidates.map { available.getValue(it.id) })
        }
        return DuplicatePreview(groups, held)
    }

    private fun readCandidates(documents: List<Uri>, check: () -> Unit): List<MotionLocal?> {
        if (documents.isEmpty()) return emptyList()
        val readers = Executors.newFixedThreadPool(minOf(2, documents.size))
        try {
            return documents.chunked(48).flatMap { wave ->
                check()
                wave.map { document ->
                    readers.submit<MotionLocal?> { inspectCandidate(document, check) }
                }.map { result ->
                    try {
                        result.get()
                    } catch (error: ExecutionException) {
                        throw error.cause ?: error
                    }
                }
            }
        } finally {
            readers.shutdownNow()
        }
    }

    private fun inspectCandidate(document: Uri, check: () -> Unit): MotionLocal? = try {
        check()
        exactJpeg(document, check)
    } catch (failure: BridgeFailure) {
        if (failure.reason == Reason.PAUSED || failure.reason == Reason.THERMAL) throw failure
        null
    } catch (_: java.io.IOException) {
        null
    } catch (_: SecurityException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }

    private fun exactJpeg(document: Uri, check: () -> Unit): MotionLocal {
        val uri = media.canonical(document)
        demand(uri.pathSegments.first() == MediaStore.VOLUME_EXTERNAL_PRIMARY &&
            uri.pathSegments[1] == "images", Reason.UNSUPPORTED)
        val source = context.contentResolver.openFileDescriptor(uri, "r")
            ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        val proof = ParcelFileDescriptor.AutoCloseInputStream(source).use {
            demand(it.channel.size() in 1..MAX_MOTION_BYTES, Reason.UNSUPPORTED)
            inspectMotionJpeg(it.channel, check)
        }
        // Extended metadata, HDR auxiliary frames, motion and unknown tails remain outside static review.
        demand(proof.video == null && !proof.existingXmp, Reason.UNSUPPORTED)
        val row = media.snapshot(uri, false, check)
        demand(row.sha == proof.still.sha && row.bytes == proof.still.bytes &&
            row.path.trimEnd('/').split('/').none { it.isBlank() || it.startsWith('.') },
            Reason.SOURCE_CHANGED)
        return row
    }

    @TargetApi(30)
    fun request(plan: DuplicatePreview, check: () -> Unit): PendingIntent {
        demand(Build.VERSION.SDK_INT >= 30 && plan.candidates.size in 1..48 &&
            ledger.duplicateJobs().none { it.state in pending }, Reason.DESTINATION_REVIEW)
        val recorded = ledger.duplicateJobs()
        val names = plan.candidates.map { it.name.lowercase(java.util.Locale.ROOT) }
        demand(names.distinct().size == names.size, Reason.DESTINATION_REVIEW)
        val targets = plan.candidates.map { it.uri.toString() }.toSet()
        demand(targets.size == plan.candidates.size && plan.groups.none { it.keeper.uri.toString() in targets } &&
            recorded.none { it.state == "MOVED_LOCAL" && JSONObject(it.keeper).getString("uri") in targets },
            Reason.DESTINATION_REVIEW)
        val jobs = plan.groups.flatMap { group ->
            demand(media.snapshot(group.keeper.uri, false, check) == group.keeper, Reason.SOURCE_CHANGED)
            group.candidates.map { candidate ->
                demand(media.snapshot(candidate.uri, false, check) == candidate, Reason.SOURCE_CHANGED)
                media.requireFreeDestination(candidate, DUPLICATE_DESTINATION)
                DuplicateJob(candidate.uri.toString(), plan.digest, group.keeper.journal(), candidate.journal(), "CONSENT")
            }
        }
        val request = MediaStore.createWriteRequest(context.contentResolver, plan.candidates.map { it.uri })
        ledger.saveDuplicateJobs(jobs)
        return request
    }

    fun finish(approved: Boolean?, check: () -> Unit) {
        val jobs = ledger.duplicateJobs().filter { it.state in pending }
        if (approved != true) {
            for (job in jobs) {
                if (job.state == "CONSENT") {
                    ledger.saveDuplicateJobs(listOf(job.copy(state = "CANCELLED")))
                } else {
                    val observed = try {
                        verifiedAfterMove(job, check)
                    } catch (_: java.io.IOException) { false } catch (_: SecurityException) { false }
                    ledger.saveDuplicateJobs(listOf(job.copy(state = if (observed) "MOVED_LOCAL" else "HOLD")))
                }
            }
            return
        }
        demand(jobs.none { it.state == "MOVE_INTENT" }, Reason.DESTINATION_REVIEW)
        for (job in jobs.filter { it.state == "CONSENT" }) {
            check()
            val candidate = try {
                readExpected(job.keeper, check)
                readExpected(job.candidate, check).also { media.requireFreeDestination(it, DUPLICATE_DESTINATION) }
            } catch (failure: BridgeFailure) {
                if (failure.reason in setOf(Reason.WIFI_REQUIRED, Reason.PAUSED, Reason.SPACE, Reason.BATTERY, Reason.THERMAL)) throw failure
                ledger.saveDuplicateJobs(listOf(job.copy(state = "HOLD")))
                continue
            }
            ledger.saveDuplicateJobs(listOf(job.copy(state = "MOVE_INTENT")))
            demand(media.moveReviewedImage(candidate, DUPLICATE_DESTINATION) == 1 &&
                verifiedAfterMove(job, check), Reason.DESTINATION_REVIEW)
            ledger.saveDuplicateJobs(listOf(job.copy(state = "MOVED_LOCAL")))
        }
    }

    private fun readExpected(saved: String, check: () -> Unit): MotionLocal {
        val json = JSONObject(saved)
        val result = media.snapshot(Uri.parse(json.getString("uri")), false, check)
        demand(result.fingerprint() == json.optString("fingerprint"), Reason.SOURCE_CHANGED)
        return result
    }

    private fun verifiedAfterMove(job: DuplicateJob, check: () -> Unit): Boolean {
        readExpected(job.keeper, check)
        val row = media.snapshot(Uri.parse(job.uri), false, check)
        val prior = JSONObject(job.candidate)
        val expectedDate = if (prior.has("date")) prior.getLong("date") else null
        return row.date == expectedDate && row.path == DUPLICATE_DESTINATION && row.name == prior.getString("name") &&
            row.sha == prior.getString("sha") && row.bytes == prior.getLong("bytes") && row.favorite == false
    }

    private companion object { val pending = setOf("CONSENT", "MOVE_INTENT") }
}
