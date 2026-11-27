package dev.gallerybridge

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import java.io.File
import java.time.OffsetDateTime
import org.json.JSONObject

internal data class MediaRow(val uri: Uri, val pending: Boolean, val size: Long, val date: Long?)

internal class MediaPublisher(private val context: Context, private val ledger: Ledger) {
    private val resolver = context.contentResolver

    private fun collection(item: Item, pending: Boolean = false): Uri {
        val base = if (item.mime.startsWith("image/"))
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        @Suppress("DEPRECATION")
        return if (pending && Build.VERSION.SDK_INT == 29) MediaStore.setIncludePending(base) else base
    }

    private fun directory(item: Item): String {
        val bucket = if (item.mime.startsWith("image/")) "Pictures" else "Movies"
        val album = ledger.edits(item.sha).singleOrNull { it.field == Field.ALBUM }?.value
        return "$bucket/${requireNotNull(item.album)}/" + (album?.let { "$it/" } ?: "")
    }

    private fun find(item: Item): MediaRow? {
        val rows = mutableListOf<MediaRow>()
        val c = collection(item)
        val args = Bundle().apply {
            putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION,
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?")
            putStringArray(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(token(item), directory(item)))
            if (Build.VERSION.SDK_INT >= 30) putInt(MediaStore.QUERY_ARG_MATCH_PENDING, MediaStore.MATCH_INCLUDE)
        }
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.IS_PENDING,
            MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
        resolver.query(collection(item, true), projection, args, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                demand(cursor.getString(4) == context.packageName, Reason.DESTINATION_REVIEW)
                rows.add(MediaRow(ContentUris.withAppendedId(c, cursor.getLong(0)),
                    cursor.getInt(1) == 1, cursor.getLong(2), cursor.getLong(3).takeIf { it > 0 }))
                demand(rows.size == 1, Reason.DESTINATION_REVIEW)
            }
        } ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        return rows.singleOrNull()
    }

    fun publish(item: Item, archive: Archive, reserve: Long, checkpoint: () -> Unit) {
        val m = archive.member(item.ordinal)
        demand(m.path == item.path && m.size == item.size && m.crc == item.crc && !m.restricted, Reason.SOURCE_CHANGED)
        val prior = find(item)
        if (item.publication != null) demand(prior?.uri?.toString() == item.publication, Reason.DESTINATION_REVIEW)
        // Recover a crash after the provider insert or after publishing but before the SQLite receipt.
        val edits = ledger.edits(item.sha)
        val explicitDate = edits.singleOrNull { it.field == Field.CAPTURE_TIME }
            ?.let { OffsetDateTime.parse(it.value).toInstant().toEpochMilli() }
        val selectedDate = explicitDate ?: publicationDate(item, ledger.getSidecar(item.sha))
        ledger.requestedDate(item.sha, selectedDate)
        val uri = prior?.uri ?: resolver.insert(collection(item), values(
            MediaStore.MediaColumns.DISPLAY_NAME to token(item), MediaStore.MediaColumns.MIME_TYPE to item.mime,
            MediaStore.MediaColumns.RELATIVE_PATH to directory(item), MediaStore.MediaColumns.IS_PENDING to 1,
            MediaStore.MediaColumns.DATE_TAKEN to selectedDate
        )) ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
        ledger.publication(item.sha, uri.toString(), "PENDING", null)

        if (prior?.pending != false) {
            val owned = find(item)
            demand(owned?.uri == uri && owned.pending, Reason.DESTINATION_REVIEW)
            val embedded = edits.filter { it.field != Field.ALBUM && item.mime.startsWith("image/") }
            val expected = if (embedded.isEmpty()) {
                val result = resolver.openOutputStream(uri, "wt")?.use { archive.verify(m, it, checkpoint) }
                demand(result?.sha == item.sha, Reason.INTEGRITY)
                requireNotNull(result)
            } else writeDerivative(item, m, uri, archive, embedded, reserve, checkpoint)
            verifyBytes(uri, expected.sha, expected.bytes, checkpoint)
            checkpoint()
            demand(resolver.update(uri, values(MediaStore.MediaColumns.IS_PENDING to 0), null, null) == 1,
                Reason.DESTINATION_REVIEW)
        }
        val receipt = ledger.item(item.sha) ?: throw BridgeFailure(Reason.SOURCE_CHANGED)
        val size = receipt.outputSize ?: item.size
        val observed = find(item)
        demand(observed != null && observed.uri == uri && !observed.pending && observed.size == size,
            Reason.DESTINATION_REVIEW)
        verifyBytes(uri, receipt.outputSha ?: item.sha, size, checkpoint)
        ledger.publication(item.sha, uri.toString(), "E1", observed?.date)
    }

    fun audit(item: Item, checkpoint: () -> Unit) {
        val row = find(item)
        if (row == null) {
            ledger.publication(item.sha, requireNotNull(item.publication), "LOCAL_ABSENT", null)
            return
        }
        demand(!row.pending && row.uri.toString() == item.publication, Reason.DESTINATION_REVIEW)
        verifyBytes(row.uri, item.outputSha ?: item.sha, item.outputSize ?: item.size, checkpoint)
        ledger.publication(item.sha, row.uri.toString(), "E1", row.date)
    }

    private fun writeDerivative(
        item: Item, member: Member, uri: Uri, archive: Archive, edits: List<PreparationEdit>,
        reserve: Long, checkpoint: () -> Unit
    ): Digest {
        val space = context.cacheDir.usableSpace - reserve
        demand(item.size <= 256 * 1024 * 1024L && item.size * 3 + 1024 * 1024 <= space, Reason.SPACE)
        val suffix = item.path.substringAfterLast('.').lowercase(java.util.Locale.ROOT)
        val file = File(context.cacheDir, "metadata-preparation.$suffix")
        try {
            val source = file.outputStream().use { archive.verify(member, it, checkpoint) }
            demand(source.sha == item.sha, Reason.INTEGRITY)
            checkpoint()
            writeAndVerifyExif(file, edits)
            checkpoint()
            val outgoing = resolver.openOutputStream(uri, "wt") ?: throw BridgeFailure(Reason.DESTINATION_REVIEW)
            val written = outgoing.use { out -> file.inputStream().use { digest(it, out, checkpoint = checkpoint) } }
            ledger.outputIdentity(item.sha, written)
            return written
        } finally {
            file.delete()
        }
    }

    private fun verifyBytes(uri: Uri, sha: String, size: Long, checkpoint: () -> Unit) {
        val result = resolver.openInputStream(uri)?.use { digest(it, maxBytes = size, checkpoint = checkpoint) }
        demand(result?.sha == sha && result.bytes == size, Reason.INTEGRITY)
    }
}

internal fun publicationDate(item: Item, sidecarJson: String?): Long? {
    val facts = item.metadata?.let(::JSONObject)
    val exif = facts?.optJSONObject("exif")
    val knownExif = exif?.epoch()
    if (knownExif != null) return knownExif
    // A wall-clock EXIF value stays in the embedded bytes without an invented offset.
    if (exif != null || item.mime.startsWith("video/")) return null
    return sidecarJson?.let(::parseSidecar)?.taken?.instant?.toEpochMilli()
}

internal fun JSONObject.epoch(): Long? = if (has("epoch_ms") && !isNull("epoch_ms")) optLong("epoch_ms") else null
