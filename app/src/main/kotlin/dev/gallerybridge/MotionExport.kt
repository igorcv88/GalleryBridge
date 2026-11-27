package dev.gallerybridge

import android.content.Context
import android.net.Uri
import java.io.File
import org.json.JSONObject

internal class MotionExport(private val context: Context, private val ledger: Ledger) {
    private val documents = Documents(context)

    fun pair(photo: Item): Item? {
        if (stillMime(photo) != "image/jpeg" || ledger.restricted(photo.sha) || ledger.excluded(photo.sha)) return null
        val id = photo.metadata?.let(::JSONObject)?.optString("live_id")?.let(::normalizeLiveId) ?: return null
        val match = ledger.livePair(photo.sha, id, false) ?: return null
        val video = ledger.item(match.sha) ?: return null
        return video.takeIf { it.mime in setOf("video/mp4", "video/quicktime") &&
            !ledger.restricted(it.sha) && !ledger.excluded(it.sha) }
    }

    fun stillMime(photo: Item): String = photo.metadata?.let(::JSONObject)
        ?.optString("actual_mime")?.takeIf(String::isNotEmpty) ?: photo.mime

    fun key(photo: Item, video: Item): String = motionDigest("motion-jpeg-v1", photo.sha, video.sha)

    private val activeJobs = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    fun stage(photo: Item, reserve: Long, check: () -> Unit): StagedMotion {
        val video = pair(photo) ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        val key = key(photo, video)
        demand(activeJobs.add(key), Reason.DESTINATION_REVIEW)
        return try { stagePair(photo, video, key, reserve, check) } finally { activeJobs.remove(key) }
    }

    private fun stagePair(photo: Item, video: Item, key: String, reserve: Long, check: () -> Unit): StagedMotion {
        val total = photo.size + video.size
        demand(total in 1..(MAX_MOTION_BYTES - 4096) && 2 * total + 4096 < context.filesDir.usableSpace - reserve, Reason.SPACE)
        val directory = File(context.filesDir, "motion-staging").apply { mkdirs() }
        val final = File(directory, "$key.jpg")
        fun verified(file: File): StagedMotion {
            val proof = inspectMotionJpeg(file, check)
            demand(proof.still == Digest(photo.sha, photo.size, photo.crc) &&
                proof.video == Digest(video.sha, video.size, video.crc), Reason.INTEGRITY)
            val result = file.inputStream().use { digest(it, maxBytes = total + 4096, checkpoint = check) }
            return StagedMotion(key, file, result)
        }
        if (final.isFile) return verified(final)
        check()
        val stillFile = File(directory, "$key.still")
        val videoFile = File(directory, "$key.video")
        val output = File(directory, "$key.tmp")
        try {
            val still = extract(photo, stillFile, reserve, check)
            val motion = extract(video, videoFile, reserve, check)
            val primary = inspectMotionJpeg(stillFile, check)
            demand(!primary.existingXmp && primary.video == null && primary.still == still, Reason.UNSUPPORTED)
            requireReadableStill(stillFile)
            validateMotionVideo(videoFile)
            requireVideoTrack(videoFile)
            output.outputStream().use { target ->
                stillFile.inputStream().use { image ->
                    videoFile.inputStream().use { movie ->
                        writeJpegMotionPhoto(image, still, movie, motion, video.mime, target, check)
                    }
                }
                target.fd.sync()
            }
            val staged = verified(output)
            demand(output.renameTo(final), Reason.NEEDS_USER_ACTION)
            return staged.copy(file = final)
        } finally {
            stillFile.delete()
            videoFile.delete()
            output.delete()
        }
    }

    fun staged(photo: Item, video: Item): Boolean =
        File(context.filesDir, "motion-staging/${key(photo, video)}.jpg").isFile

    fun export(photo: Item, destination: Uri, reserve: Long, check: () -> Unit) {
        val output = stage(photo, reserve, check)
        val resolver = context.contentResolver
        val target = resolver.openOutputStream(destination, "wt") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        target.use { out ->
            output.file.inputStream().use {
                demand(digest(it, out, output.digest.bytes, check) == output.digest, Reason.INTEGRITY)
            }
        }
        val written = resolver.openInputStream(destination)?.use { digest(it, maxBytes = output.digest.bytes, checkpoint = check) }
        demand(written == output.digest, Reason.INTEGRITY)
    }

    fun verifyArchives(photo: Item, video: Item, reserve: Long, check: () -> Unit) {
        demand(pair(photo)?.sha == video.sha, Reason.DESTINATION_REVIEW)
        for (item in listOf(photo, video)) {
            documents.seekable(Uri.parse(item.source.uri), check, reserve) { channel ->
                Archive(channel).use { zip ->
                    val member = zip.member(item.ordinal)
                    demand(member.path == item.path && !member.restricted && member.crc == item.crc &&
                        member.size == item.size, Reason.SOURCE_CHANGED)
                    demand(zip.verify(member, checkpoint = check).sha == item.sha, Reason.INTEGRITY)
                }
            }
        }
    }

    private fun extract(item: Item, file: File, reserve: Long, check: () -> Unit): Digest =
        documents.seekable(Uri.parse(item.source.uri), check, reserve) { channel ->
            Archive(channel).use { zip ->
                val member = zip.member(item.ordinal)
                demand(member.path == item.path && !member.restricted && member.crc == item.crc &&
                    member.size == item.size, Reason.SOURCE_CHANGED)
                file.outputStream().use { zip.verify(member, it, check) }.also {
                    demand(it.sha == item.sha, Reason.INTEGRITY)
                }
            }
        }
}


internal data class StagedMotion(val key: String, val file: File, val digest: Digest)
internal const val MAX_MOTION_BYTES = 256 * 1024 * 1024L

internal fun requireVideoTrack(file: File) =
    file.inputStream().use { requireVideoTrack(it.fd, 0, file.length()) }

internal fun requireVideoTrack(descriptor: java.io.FileDescriptor, offset: Long, length: Long) {
    val extractor = android.media.MediaExtractor()
    try {
        extractor.setDataSource(descriptor, offset, length)
        demand((0 until extractor.trackCount).any {
            extractor.getTrackFormat(it).getString(android.media.MediaFormat.KEY_MIME)?.startsWith("video/") == true
        }, Reason.UNSUPPORTED)
    } finally {
        extractor.release()
    }
}

internal fun requireReadableStill(file: File) =
    file.inputStream().use { requireReadableStill(it) }

internal fun requireReadableStill(source: java.io.FileInputStream) {
    val options = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
    source.channel.position(0)
    android.graphics.BitmapFactory.decodeFileDescriptor(source.fd, null, options)
    demand(options.outWidth > 0 && options.outHeight > 0 && options.outMimeType == "image/jpeg", Reason.INTEGRITY)
    var sample = 1
    while (maxOf(options.outWidth, options.outHeight) / sample > 1440) sample *= 2
    options.inJustDecodeBounds = false
    options.inSampleSize = sample
    source.channel.position(0)
    val bitmap = android.graphics.BitmapFactory.decodeFileDescriptor(source.fd, null, options)
        ?: throw BridgeFailure(Reason.INTEGRITY)
    bitmap.recycle()
}
