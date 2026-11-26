package dev.gallerybridge

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.io.Writer
import java.time.Instant
import org.json.JSONObject

internal fun exportCatalog(db: SQLiteDatabase, writer: Writer, checkpoint: () -> Unit) {
    var records = 0L
    fun emit(row: JSONObject) {
        checkpoint()
        writer.append(row.toString()).append('\n')
        records++
    }
    emit(JSONObject().put("type", "header").put("schema", "gallerybridge.catalog")
        .put("schema_version", 1).put("exported_at", Instant.now().toString()))
    // sqlite URIs, URLs, raw sidecars, names and metadata can include sensitive personal data.
    // Include proven binary identities, but omit user locations, account records and source grants.
    val views = listOf(
        "archive" to "SELECT id AS archive_id,sha AS zip_sha256,bytes,state FROM sources ORDER BY id",
        "entry" to """SELECT source AS archive_id, ordinal,sha AS sha256,size AS bytes,crc AS crc32,
                      mime,modified AS zip_modified_ms,restricted,format_verified FROM entries
                      WHERE source IN (SELECT id FROM sources WHERE state='VERIFIED') ORDER BY source,ordinal""",
        "decision" to "SELECT sha AS sha256,field,action,scope,approved_at FROM decisions ORDER BY sha,field",
        "publication" to "SELECT sha AS sha256,batch,state,observed AS date_taken_observed_ms,output_sha AS derivative_sha256,output_size AS derivative_bytes FROM publication ORDER BY batch,sha"
    )
    for ((type, query) in views) {
        db.rawQuery(query, null).use { row ->
            while (row.moveToNext()) {
                val json = JSONObject().put("type", type)
                for (col in 0 until row.columnCount) {
                    val value = when (row.getType(col)) {
                        Cursor.FIELD_TYPE_INTEGER -> row.getLong(col)
                        Cursor.FIELD_TYPE_STRING -> row.getString(col)
                        else -> JSONObject.NULL
                    }
                    json.put(row.getColumnName(col), value)
                }
                emit(json)
            }
        }
    }
    emit(JSONObject().put("type", "end").put("records_before_end", records))
}
