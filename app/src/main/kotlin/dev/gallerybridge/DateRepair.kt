package dev.gallerybridge

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

internal data class DateReview(val source: Uri, val identity: Digest, val original: String, val takeout: String?, val evidence: String?)
internal data class DateCopyPlan(val review: DateReview, val date: OffsetDateTime, val output: Digest)

internal class DateRepair(private val context: Context, private val ledger: Ledger) {
    private val resolver = context.contentResolver

    private fun read(uri: Uri, check: () -> Unit): Pair<Digest, ByteArray> {
        val stream = resolver.openInputStream(uri) ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        return stream.use { input ->
            ByteArrayOutputStream().use { buffer ->
                val proof = digest(input, buffer, 12L * 1024 * 1024, check)
                proof to buffer.toByteArray()
            }
        }
    }

    fun preview(uri: Uri, check: () -> Unit): DateReview {
        val (identity, bytes) = read(uri, check)
        val exif = ExifInterface(bytes.inputStream())
        val raw = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL).orEmpty()
        val zone = exif.getAttribute(ExifInterface.TAG_OFFSET_TIME_ORIGINAL)
        val time = cameraTime(raw, zone, "EXIF")
        val takeout = ledger.originalIdentity(identity)?.let { item ->
            ledger.getSidecar(item.sha)?.let(::parseSidecar)?.taken?.instant
        }
        val note = when {
            time.instant == null -> "O arquivo não comprova uma data com fuso. Confira a fonte antes de escolher."
            takeout == null -> null
            time.instant != takeout -> "A data do Takeout diverge da data embutida. A escolha exige confirmação."
            else -> "O instante do Takeout coincide com o original."
        }
        return DateReview(uri, identity, listOfNotNull(raw.takeIf(String::isNotBlank), zone).joinToString(" "), takeout?.toString(), note)
    }

    fun plan(review: DateReview, typed: String, check: () -> Unit): DateCopyPlan {
        val date = runCatching { OffsetDateTime.parse(typed.trim()) }.getOrNull()
            ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        val result = prepare(review, date, check)
        return DateCopyPlan(review, date, digest(result.inputStream(), checkpoint = check))
    }

    private fun prepare(review: DateReview, date: OffsetDateTime, check: () -> Unit): ByteArray {
        val (identity, original) = read(review.source, check)
        demand(identity == review.identity, Reason.SOURCE_CHANGED)
        val file = File.createTempFile("data-preview-", ".jpg", context.cacheDir)
        try {
            file.writeBytes(original)
            val proof = inspectMotionJpeg(file, check)
            demand(proof.video == null && !proof.existingXmp, Reason.UNSUPPORTED)
            requireReadableStill(file)
        } finally { file.delete() }
        val fixed = patchExifCapture(original, date)
        val exif = ExifInterface(fixed.inputStream())
        val expected = date.format(DateTimeFormatter.ofPattern("uuuu:MM:dd HH:mm:ss", Locale.ROOT))
        val offset = if (date.offset == ZoneOffset.UTC) "+00:00" else date.offset.id
        val subsec = exif.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL).orEmpty()
        demand(exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) == expected &&
            exif.getAttribute(ExifInterface.TAG_OFFSET_TIME_ORIGINAL) == offset &&
            subsec.padEnd(9, '0').toIntOrNull() == date.nano, Reason.INTEGRITY)
        return fixed
    }

    fun export(plan: DateCopyPlan, destination: Uri, check: () -> Unit) {
        demand(destination != plan.review.source, Reason.DESTINATION_REVIEW)
        val output = prepare(plan.review, plan.date, check)
        demand(digest(output.inputStream(), checkpoint = check) == plan.output, Reason.SOURCE_CHANGED)
        val empty = resolver.openInputStream(destination)?.use { it.read() == -1 }
        demand(empty == true, Reason.DESTINATION_REVIEW)
        val target = resolver.openOutputStream(destination, "wt") ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        target.use { sink ->
            demand(digest(output.inputStream(), sink, plan.output.bytes, check) == plan.output, Reason.INTEGRITY)
        }
        demand(read(destination, check).first == plan.output, Reason.INTEGRITY)
    }
}
