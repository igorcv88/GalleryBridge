package dev.gallerybridge

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject

internal data class BatchProgress(val id: Long, val prepared: Int, val planned: Int, val bytes: Long, val absent: Int, val state: String)

internal data class Source(val id: Long, val uri: String, val sha: String?, val state: String, val name: String? = null)
internal data class Item(
    val sha: String, val source: Source, val ordinal: Int, val path: String,
    val size: Long, val crc: Long, val mime: String,
    val metadata: String?, val modified: Long?, val publication: String?,
    val state: String, val album: String?, val observed: Long?,
    val outputSha: String? = null, val outputSize: Long? = null
)

internal data class FavoriteSource(val part: Long, val entry: Int, val flags: Set<Boolean>, val photosKey: String?)

private val favoriteSidecars = """
    SELECT e.sha,j.source,j.ordinal,j.json FROM entries e
    JOIN sources s ON s.id=e.source AND s.state='VERIFIED'
    JOIN entries j ON j.source=e.source AND j.directory=e.directory AND j.base=e.name
    WHERE e.mime IS NOT NULL AND e.restricted=0 AND j.json IS NOT NULL AND j.restricted=0
      AND NOT EXISTS(SELECT 1 FROM entries x WHERE x.sha=e.sha AND x.restricted=1)
      AND (SELECT count(*) FROM entries m WHERE m.source=e.source AND m.directory=e.directory AND m.name=e.name)=1
""".trimIndent()

internal class Ledger(context: Context, name: String = "gallerybridge.db") : SQLiteOpenHelper(
    context, name, null, 3
) {
    init { setWriteAheadLoggingEnabled(true) }

    override fun onConfigure(db: SQLiteDatabase) = db.setForeignKeyConstraintsEnabled(true)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE sources(id INTEGER PRIMARY KEY, uri TEXT UNIQUE NOT NULL, sha TEXT, state TEXT NOT NULL, bytes INTEGER)")
        db.execSQL("""
            CREATE TABLE entries(
                source INTEGER NOT NULL REFERENCES sources(id), ordinal INTEGER NOT NULL,
                path TEXT NOT NULL, directory TEXT NOT NULL, name TEXT NOT NULL, size INTEGER NOT NULL,
                crc INTEGER NOT NULL, sha TEXT NOT NULL, mime TEXT, modified INTEGER, restricted INTEGER NOT NULL,
                json TEXT, base TEXT, metadata TEXT, live_id TEXT, format_verified INTEGER NOT NULL,
                PRIMARY KEY(source,ordinal))
        """.trimIndent())
        db.execSQL("CREATE INDEX entry_sha ON entries(sha, source, ordinal)")
        db.execSQL("CREATE INDEX entry_base ON entries(source, directory, base)")
        db.execSQL("CREATE INDEX entry_live ON entries(live_id)")
        db.execSQL("CREATE TABLE local_match(sha TEXT PRIMARY KEY)")
        db.execSQL("CREATE TABLE reviews(sha TEXT PRIMARY KEY, state TEXT NOT NULL)")
        db.execSQL("CREATE TABLE batches(id INTEGER PRIMARY KEY, state TEXT NOT NULL, visual INTEGER NOT NULL DEFAULT 0, created INTEGER NOT NULL)")
        db.execSQL("""
            CREATE TABLE publication(
                sha TEXT PRIMARY KEY, batch INTEGER NOT NULL REFERENCES batches(id), token TEXT UNIQUE NOT NULL,
                source INTEGER NOT NULL, ordinal INTEGER NOT NULL, uri TEXT, state TEXT NOT NULL, observed INTEGER,
                FOREIGN KEY(source,ordinal) REFERENCES entries(source,ordinal))
        """.trimIndent())
        db.execSQL("CREATE INDEX publication_batch ON publication(batch)")
        db.execSQL("""
            CREATE TABLE decisions(
                sha TEXT NOT NULL, field TEXT NOT NULL, action TEXT NOT NULL,
                scope TEXT NOT NULL, approved_at INTEGER NOT NULL, PRIMARY KEY(sha,field))
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE remote_pending(
                id TEXT PRIMARY KEY, state TEXT NOT NULL CHECK(state IN ('UNRESOLVED')))
        """.trimIndent())
        createReconciliationTables(db)
        createFavoriteEvidence(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        require(oldVersion in 1..2 && newVersion == 3)
        if (oldVersion == 1) createReconciliationTables(db)
        createFavoriteEvidence(db)
    }

    private fun createFavoriteEvidence(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE favorite_evidence(
                sha TEXT NOT NULL, level TEXT NOT NULL, state TEXT NOT NULL, observed_at INTEGER NOT NULL,
                PRIMARY KEY(sha,level))
        """.trimIndent())
    }

    fun recordFavoriteEvidence(sha: String, level: FavoriteObservationLevel, state: String) {
        require(level != FavoriteObservationLevel.REMOTE_INDEPENDENT)
        writableDatabase.execSQL("INSERT OR REPLACE INTO favorite_evidence VALUES(?,?,?,?)",
            arrayOf<Any?>(sha, level.name, state, System.currentTimeMillis()))
    }

    fun favoriteEvidence(sha: String): Map<FavoriteObservationLevel, String> = readableDatabase.rawQuery(
        "SELECT level,state FROM favorite_evidence WHERE sha=?", arrayOf(sha)
    ).use { row -> buildMap {
        while (row.moveToNext()) put(FavoriteObservationLevel.valueOf(row.getString(0)), row.getString(1))
    } }

    fun favoriteSourceEvidence(sha: String): List<FavoriteSource> =
        readableDatabase.rawQuery(favoriteSidecars + " AND e.sha=? ORDER BY j.source,j.ordinal", arrayOf(sha)).use { row ->
            buildList {
                while (row.moveToNext()) {
                    val raw = row.getString(3)
                    val flags = parseSidecar(raw)?.favoriteObservations.orEmpty()
                    if (flags.isNotEmpty()) {
                        val key = googlePhotosItemKey(JSONObject(raw).optString("url"))
                        add(FavoriteSource(row.getLong(1), row.getInt(2), flags, key))
                    }
                }
            }
        }

    private fun createReconciliationTables(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX entry_media_name ON entries(source,directory,name)")
        db.execSQL("CREATE TABLE local_target(sha TEXT NOT NULL, uri TEXT NOT NULL, PRIMARY KEY(sha,uri))")
        db.execSQL("""
            CREATE TABLE favorite_attempt(
                sha TEXT PRIMARY KEY, uri TEXT NOT NULL, desired INTEGER NOT NULL, state TEXT NOT NULL,
                confirmed INTEGER NOT NULL DEFAULT 0, observed_at INTEGER NOT NULL)
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE preparation_edit(
                sha TEXT NOT NULL, field TEXT NOT NULL, payload TEXT NOT NULL,
                PRIMARY KEY(sha,field))
        """.trimIndent())
        db.execSQL("ALTER TABLE publication ADD COLUMN output_sha TEXT")
        db.execSQL("ALTER TABLE publication ADD COLUMN output_size INTEGER")
        db.execSQL("ALTER TABLE publication ADD COLUMN requested_date INTEGER")
        db.execSQL("ALTER TABLE sources ADD COLUMN title TEXT")
    }

    fun addSource(uri: String, title: String? = null) {
        writableDatabase.insertWithOnConflict("sources", null, values("uri" to uri, "state" to "SELECTED", "title" to title), SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun sources(): List<Source> = readableDatabase.rawQuery(
        "SELECT id,uri,sha,state,title FROM sources ORDER BY id", null
    ).use { row ->
        buildList { while (row.moveToNext()) add(Source(row.getLong(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4))) }
    }

    fun sourceState(id: Long, state: String) = writableDatabase.execSQL(
        "UPDATE sources SET state=? WHERE id=?", arrayOf<Any?>(state, id)
    )

    fun prepareSource(source: Source, raw: Digest) {
        demand(source.sha == null || source.sha == raw.sha, Reason.SOURCE_CHANGED)
        writableDatabase.execSQL("UPDATE sources SET sha=?,bytes=?,state='READING' WHERE id=?",
            arrayOf<Any?>(raw.sha, raw.bytes, source.id))
    }

    fun nextOrdinal(id: Long): Int = readableDatabase.rawQuery(
        "SELECT COALESCE(MAX(ordinal)+1,0) FROM entries WHERE source=?", arrayOf(id.toString())
    ).use { it.moveToFirst(); it.getInt(0) }

    fun addEntry(id: Long, m: Member, sha: String, json: String?, metadata: String?, liveId: String?, formatVerified: Int) {
        writableDatabase.insertOrThrow("entries", null, values(
            "source" to id, "ordinal" to m.ordinal, "path" to m.path,
            "directory" to m.directory, "name" to m.name, "size" to m.size,
            "crc" to m.crc, "sha" to sha, "mime" to m.mime,
            "modified" to m.modified?.toEpochMilli(), "restricted" to (if (m.restricted) 1 else 0),
            "json" to json, "base" to sidecarBase(m.name), "metadata" to metadata, "live_id" to liveId, "format_verified" to formatVerified
        ))
    }

    fun getSidecar(sha: String): String? = readableDatabase.rawQuery("""
        SELECT DISTINCT j.json FROM entries e JOIN sources s ON s.id=e.source AND s.state='VERIFIED'
        JOIN entries j ON j.source=e.source AND j.directory=e.directory AND j.base=e.name
        WHERE e.sha=? AND j.json IS NOT NULL AND j.restricted=0
          AND (SELECT count(*) FROM entries m WHERE m.source=e.source AND m.path=e.path)=1
        LIMIT 2
    """.trimIndent(), arrayOf(sha)).use {
        if (it.count == 1 && it.moveToFirst()) it.getString(0) else null
    }

    private val itemQuery = """
        SELECT e.sha,s.id,s.uri,s.sha,e.ordinal,e.path,e.size,e.crc,e.mime,e.metadata,e.modified,
               p.uri,p.state,p.batch,p.observed,p.output_sha,p.output_size
        FROM entries e JOIN sources s ON e.source=s.id LEFT JOIN publication p ON p.sha=e.sha
    """.trimIndent()

    fun item(sha: String): Item? = readItems(
        "$itemQuery WHERE e.sha=? AND e.mime IS NOT NULL AND s.state='VERIFIED' ORDER BY e.source,e.ordinal LIMIT 1",
        arrayOf(sha)
    ).firstOrNull()

    fun originalIdentity(digest: Digest): Item? {
        val candidates = buildSet {
            item(digest.sha)?.takeIf { it.size == digest.bytes }?.let { add(it.sha) }
            readableDatabase.rawQuery(
                "SELECT sha FROM publication WHERE state IN ('E1','LOCAL_ABSENT') AND output_sha=? AND output_size=?",
                arrayOf(digest.sha, digest.bytes.toString())
            ).use { row -> while (row.moveToNext()) add(row.getString(0)) }
        }
        return candidates.singleOrNull()?.let(::item)
    }

    fun page(offset: Int, limit: Int = 25) = readItems("""
        WITH ids AS (SELECT e.sha, MIN(e.rowid) AS rowid FROM entries e JOIN sources s ON s.id=e.source
            WHERE s.state='VERIFIED' AND e.mime IS NOT NULL AND e.restricted=0
            GROUP BY e.sha ORDER BY e.sha LIMIT $limit OFFSET $offset)
        $itemQuery JOIN ids ON e.rowid=ids.rowid ORDER BY e.sha
    """.trimIndent())

    fun preparationCandidates(source: Long, mime: String): List<Item> = readItems("""
        WITH ids AS (SELECT e.sha, MIN(e.rowid) AS rowid FROM entries e JOIN sources s ON s.id=e.source
            WHERE s.state='VERIFIED' AND e.restricted=0 AND e.source=? AND e.mime=?
            GROUP BY e.sha)
        $itemQuery JOIN ids ON e.rowid=ids.rowid WHERE p.sha IS NULL ORDER BY e.sha
    """.trimIndent(), arrayOf(source.toString(), mime))

    fun albumPaths(sha: String): List<String> = readableDatabase.rawQuery("""
        SELECT DISTINCT e.directory FROM entries e JOIN sources s ON s.id=e.source
        WHERE e.sha=? AND e.restricted=0 AND s.state='VERIFIED' ORDER BY e.directory
    """.trimIndent(), arrayOf(sha)).use { row ->
        buildList { while (row.moveToNext()) add(row.getString(0)) }
    }

    fun approvedVideo(sha: String): Boolean = scalar(
        "SELECT count(*) FROM reviews WHERE sha=? AND state='VIDEO_APPROVED'", arrayOf(sha)) == 1L

    fun exclude(sha: String, excluded: Boolean) {
        if (excluded) writableDatabase.execSQL(
            "INSERT OR REPLACE INTO reviews VALUES(?,'EXCLUDED')", arrayOf(sha))
        else writableDatabase.execSQL("DELETE FROM reviews WHERE sha=? AND state='EXCLUDED'", arrayOf(sha))
    }

    fun excluded(sha: String) = scalar(
        "SELECT count(*) FROM reviews WHERE sha=? AND state='EXCLUDED'", arrayOf(sha)) == 1L

    fun restricted(sha: String) = scalar(
        "SELECT count(*) FROM entries WHERE sha=? AND restricted=1", arrayOf(sha)) != 0L

    fun livePair(sha: String, id: String, video: Boolean): MediaId? {
        val group = readableDatabase.rawQuery(
            """
            SELECT DISTINCT e.sha, CASE WHEN e.mime LIKE 'video/%' THEN 1 ELSE 0 END FROM entries e
            JOIN sources s ON s.id=e.source AND s.state='VERIFIED'
            WHERE e.live_id=? AND e.restricted=0 AND e.mime IS NOT NULL LIMIT 3
            """.trimIndent(), arrayOf(id)
        ).use { row -> buildList {
            while (row.moveToNext()) add(MediaId(row.getString(0), row.getInt(1) == 1, id))
        } }
        return dev.gallerybridge.livePair(MediaId(sha, video, id), group)
    }

    fun approveVideo(sha: String) = writableDatabase.execSQL(
        "INSERT OR REPLACE INTO reviews VALUES(?,'VIDEO_APPROVED')", arrayOf(sha))

    fun localTarget(sha: String, uri: String) = writableDatabase.execSQL(
        "INSERT OR IGNORE INTO local_target VALUES(?,?)", arrayOf(sha, uri))

    fun localTargets(sha: String): List<String> = readableDatabase.rawQuery(
        "SELECT uri FROM local_target WHERE sha=? ORDER BY uri", arrayOf(sha)
    ).use { row -> buildList { while (row.moveToNext()) add(row.getString(0)) } }

    fun favoriteSources(checkpoint: () -> Unit): Map<String, HistoricalFavorite> {
        val observations = mutableMapOf<String, MutableSet<Boolean>>()
        readableDatabase.rawQuery(favoriteSidecars, null).use { row ->
            while (row.moveToNext()) {
                checkpoint()
                val flags = parseSidecar(row.getString(3))?.favoriteObservations.orEmpty()
                if (flags.isNotEmpty()) observations.getOrPut(row.getString(0)) { mutableSetOf() }.addAll(flags)
            }
        }
        return observations.mapValues { historicalFavorite(it.value) }
    }

    fun favoriteRequests(changes: List<FavoriteChange>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            demand(pendingFavorites().isEmpty(), Reason.DESTINATION_REVIEW)
            changes.forEach { change ->
                db.execSQL("INSERT OR REPLACE INTO favorite_attempt VALUES(?,?,?,?,0,?)",
                    arrayOf<Any?>(change.sha, change.uri, if (change.desired) 1 else 0,
                        FavoriteState.AWAITING_CONSENT.name, System.currentTimeMillis()))
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun pendingFavorites(): List<FavoriteChange> = readableDatabase.rawQuery(
        "SELECT sha,uri,desired FROM favorite_attempt WHERE state='AWAITING_CONSENT' ORDER BY sha", null
    ).use { row -> buildList {
        while (row.moveToNext()) add(FavoriteChange(row.getString(0), row.getString(1), row.getInt(2) != 0))
    } }

    fun favoriteReceipt(change: FavoriteChange, state: FavoriteState, consent: Boolean) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "UPDATE favorite_attempt SET state=?,confirmed=?,observed_at=? WHERE sha=? AND uri=? AND desired=?",
                arrayOf<Any?>(state.name, if (consent) 1 else 0, System.currentTimeMillis(),
                    change.sha, change.uri, if (change.desired) 1 else 0))
            val observed = when (state) {
                FavoriteState.LOCAL_VERIFIED, FavoriteState.ALREADY_LOCAL -> change.desired
                FavoriteState.NOT_APPLIED -> !change.desired
                else -> null
            }
            recordFavoriteEvidence(change.sha, FavoriteObservationLevel.LOCAL_STATE, when (observed) {
                true -> "FAVORITED"
                false -> "NOT_FAVORITED"
                null -> "UNKNOWN"
            })
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun favoriteStatus(sha: String): FavoriteState? = readableDatabase.rawQuery(
        "SELECT state FROM favorite_attempt WHERE sha=?", arrayOf(sha)
    ).use { if (it.moveToFirst()) FavoriteState.valueOf(it.getString(0)) else null }

    fun localMatch(sha: String) = writableDatabase.execSQL("INSERT OR IGNORE INTO local_match VALUES(?)", arrayOf(sha))

    fun plan(limit: Int, budget: Long): List<Item> {
        val eligible = readItems("""
            WITH ids AS (SELECT e.sha, MIN(e.rowid) AS rowid FROM entries e JOIN sources s ON s.id=e.source
                WHERE s.state='VERIFIED' AND e.mime IS NOT NULL AND e.restricted=0
                  AND NOT EXISTS(SELECT 1 FROM entries other WHERE other.sha=e.sha AND other.restricted=1)
                  AND e.format_verified=1
                  AND NOT EXISTS(SELECT 1 FROM reviews r WHERE r.sha=e.sha AND r.state='EXCLUDED')
                  AND NOT EXISTS(SELECT 1 FROM publication p WHERE p.sha=e.sha)
                  AND NOT EXISTS(SELECT 1 FROM local_match l WHERE l.sha=e.sha)
                  AND (e.mime LIKE 'image/%' OR EXISTS(SELECT 1 FROM reviews r
                      WHERE r.sha=e.sha AND r.state='VIDEO_APPROVED'))
                  AND (e.live_id IS NULL OR EXISTS(SELECT 1 FROM reviews r
                      WHERE r.sha=e.sha AND r.state='VIDEO_APPROVED'))
                GROUP BY e.sha ORDER BY e.sha LIMIT $limit)
            $itemQuery JOIN ids ON e.rowid=ids.rowid ORDER BY e.sha
        """.trimIndent())
        var remaining = budget
        return eligible.filter {
            val fits = it.size in 1..remaining
            if (fits) remaining -= it.size
            fits
        }
    }

    fun batchProgress(): List<BatchProgress> = readableDatabase.rawQuery("""
        SELECT b.id,COALESCE(SUM(CASE WHEN p.state IN ('E1','LOCAL_ABSENT') THEN 1 ELSE 0 END),0),
          count(p.sha),COALESCE(SUM(CASE WHEN p.state='E1' THEN COALESCE(p.output_size,e.size) ELSE 0 END),0),
          COALESCE(SUM(CASE WHEN p.state='LOCAL_ABSENT' THEN 1 ELSE 0 END),0),b.state
        FROM batches b LEFT JOIN publication p ON p.batch=b.id LEFT JOIN entries e ON e.source=p.source AND e.ordinal=p.ordinal
        GROUP BY b.id ORDER BY b.id DESC
    """.trimIndent(), null).use { row -> buildList {
        while (row.moveToNext()) add(BatchProgress(row.getLong(0), row.getInt(1), row.getInt(2),
            row.getLong(3), row.getInt(4), row.getString(5)))
    } }

    fun activeBatch(): Long? = scalar(
        "SELECT COALESCE(MIN(id),0) FROM batches WHERE state='STAGING'").takeIf { it != 0L }

    fun lastBatch(): Long? = scalar("SELECT COALESCE(MAX(id),0) FROM batches").takeIf { it != 0L }
    fun hasReviewedPilot() = scalar("SELECT count(*) FROM batches WHERE visual=1") > 0
    fun attest(id: Long) = writableDatabase.execSQL(
        "UPDATE batches SET visual=1 WHERE id=? AND state='WAITING_NATIVE_SYNC'", arrayOf(id))

    fun createBatch(items: List<Item>): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            demand(activeBatch() == null, Reason.DESTINATION_REVIEW)
            val id = db.insertOrThrow("batches", null,
                values("state" to "STAGING", "created" to System.currentTimeMillis()))
            for (item in items) {
                db.insertOrThrow("publication", null, values(
                    "sha" to item.sha, "batch" to id, "token" to token(item), "source" to item.source.id,
                    "ordinal" to item.ordinal, "state" to "PLANNED"
                ))
            }
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun pending(batch: Long) = readItems(
        "$itemQuery WHERE p.batch=? AND p.source=e.source AND p.ordinal=e.ordinal AND p.state NOT IN ('E1','LOCAL_ABSENT') ORDER BY s.id,e.ordinal",
        arrayOf(batch.toString())
    )

    fun published(): List<Item> = readItems(
        "$itemQuery WHERE p.source=e.source AND p.ordinal=e.ordinal AND p.state IN ('E1','LOCAL_ABSENT') ORDER BY p.batch,e.ordinal"
    )

    fun finished(batch: Long) = writableDatabase.execSQL(
        "UPDATE batches SET state='WAITING_NATIVE_SYNC' WHERE id=?", arrayOf(batch))

    fun publication(sha: String, uri: String, state: String, observed: Long?) = writableDatabase.execSQL(
        "UPDATE publication SET uri=?,state=?,observed=? WHERE sha=?", arrayOf<Any?>(uri, state, observed, sha)
    )

    fun edits(sha: String): List<PreparationEdit> = readableDatabase.rawQuery(
        "SELECT field,payload FROM preparation_edit WHERE sha=? ORDER BY field", arrayOf(sha)
    ).use { row -> buildList {
        while (row.moveToNext()) add(PreparationEdit(Field.valueOf(row.getString(0)), row.getString(1)))
    } }

    fun saveEdit(item: Item, edit: PreparationEdit) {
        demand(item(item.sha)?.state == "NEW" && !restricted(item.sha) && !excluded(item.sha), Reason.DESTINATION_REVIEW)
        writableDatabase.execSQL("INSERT OR REPLACE INTO preparation_edit VALUES(?,?,?)",
            arrayOf(item.sha, edit.field.name, edit.value))
    }

    fun clearEdit(sha: String, field: Field) {
        if (item(sha)?.state == "NEW") writableDatabase.execSQL(
            "DELETE FROM preparation_edit WHERE sha=? AND field=?", arrayOf(sha, field.name))
    }

    fun requestedDate(sha: String, date: Long?) = writableDatabase.execSQL(
        "UPDATE publication SET requested_date=? WHERE sha=?", arrayOf<Any?>(date, sha))

    fun outputIdentity(sha: String, written: Digest) = writableDatabase.execSQL(
        "UPDATE publication SET output_sha=?,output_size=? WHERE sha=?", arrayOf<Any?>(written.sha, written.bytes, sha))

    fun recordDecision(sha: String, field: Field, action: Action, scope: String) = writableDatabase.execSQL(
        "INSERT OR REPLACE INTO decisions VALUES(?,?,?,?,?)",
        arrayOf<Any?>(sha, field.name, action.name, scope, System.currentTimeMillis())
    )

    fun recordHistoricalPending() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for ((batch, count) in listOf(2 to 9, 3 to 1)) for (index in 1..count) {
                db.execSQL("INSERT OR IGNORE INTO remote_pending VALUES(?,'UNRESOLVED')",
                    arrayOf("historical-$batch-$index"))
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun summary() = JSONObject()
        .put("schema_version", 1)
        .put("archives_verified", scalar("SELECT count(*) FROM sources WHERE state='VERIFIED'"))
        .put("archives_pending", scalar("SELECT count(*) FROM sources WHERE state<>'VERIFIED'"))
        .put("media_unique", scalar("SELECT count(DISTINCT e.sha) FROM entries e JOIN sources s ON e.source=s.id WHERE e.mime IS NOT NULL AND s.state='VERIFIED'"))
        .put("media_entries", scalar("SELECT count(*) FROM entries e JOIN sources s ON e.source=s.id WHERE e.mime IS NOT NULL AND s.state='VERIFIED'"))
        .put("unsupported_or_unrecognized", scalar("SELECT count(DISTINCT e.sha) FROM entries e JOIN sources s ON s.id=e.source WHERE e.mime IS NOT NULL AND e.format_verified=0 AND s.state='VERIFIED'"))
        .put("protected_identities", scalar("SELECT count(DISTINCT e.sha) FROM entries e JOIN sources s ON s.id=e.source WHERE e.mime IS NOT NULL AND e.restricted=1 AND s.state='VERIFIED'"))
        .put("media_bytes_unique", scalar("SELECT COALESCE(SUM(size),0) FROM (SELECT MAX(e.size) AS size FROM entries e JOIN sources s ON s.id=e.source WHERE e.mime IS NOT NULL AND s.state='VERIFIED' GROUP BY e.sha)"))
        .put("local_exact_matches", scalar("SELECT count(*) FROM local_match"))
        .put("historical_cloud_pending", scalar("SELECT count(*) FROM remote_pending"))
        .put("media_store_receipts", scalar("SELECT count(*) FROM publication WHERE state IN ('E1','LOCAL_ABSENT')"))
        .put("local_absence_unknown", scalar("SELECT count(*) FROM publication WHERE state='LOCAL_ABSENT'"))
        .put("hand_reviewed_batches", scalar("SELECT count(*) FROM batches WHERE visual=1"))
        .put("cloud_evidence", "UNSUPPORTED")
        .put("cloud_verified", 0)
        .put("free_up_eligible", false)
        .put("favorite_local_verified", scalar("SELECT count(*) FROM favorite_attempt WHERE state='LOCAL_VERIFIED'"))
        .put("favorite_awaiting_consent", scalar("SELECT count(*) FROM favorite_attempt WHERE state='AWAITING_CONSENT'"))
        .put("favorite_sdk_clean_positive", scalar(
            "SELECT count(*) FROM favorite_evidence WHERE level='SDK_CACHE' AND state='CLEAN_FAVORITE'"))
        .put("favorite_remote_visual_positive", scalar(
            "SELECT count(*) FROM favorite_evidence WHERE level='REMOTE_VISUAL' AND state='FAVORITED'"))
        .put("favorite_remote_verified", 0)
        .put("prepared_metadata_edits", scalar("SELECT count(*) FROM preparation_edit"))
        .put("verified_derivative_copies", scalar("SELECT count(*) FROM publication WHERE state='E1' AND output_sha IS NOT NULL"))
        .put("indexed_dates_verified", scalar("SELECT count(*) FROM publication WHERE state='E1' AND requested_date IS NOT NULL AND abs(observed-requested_date)<=2000"))
        .put("indexed_dates_review", scalar("SELECT count(*) FROM publication WHERE state='E1' AND requested_date IS NOT NULL AND (observed IS NULL OR abs(observed-requested_date)>2000)"))
        .put("field_decisions", scalar("SELECT count(*) FROM decisions"))

    private fun scalar(sql: String, args: Array<String>? = null) = readableDatabase.rawQuery(sql, args)
        .use { it.moveToFirst(); it.getLong(0) }

    private fun readItems(sql: String, args: Array<String>? = null): List<Item> =
        readableDatabase.rawQuery(sql, args).use { row ->
            buildList {
                while (row.moveToNext()) {
                    val sha = row.getString(0)
                    add(Item(sha, Source(row.getLong(1), row.getString(2), row.getString(3), "VERIFIED"),
                        row.getInt(4), row.getString(5), row.getLong(6), row.getLong(7), row.getString(8),
                        row.getString(9), if (row.isNull(10)) null else row.getLong(10),
                        row.getString(11), row.getString(12) ?: "NEW",
                        if (row.isNull(13)) null else "GalleryBridge_%03d".format(java.util.Locale.ROOT, row.getLong(13)),
                        if (row.isNull(14)) null else row.getLong(14), row.getString(15),
                        if (row.isNull(16)) null else row.getLong(16)))
                }
            }
        }
}

internal fun token(item: Item): String =
    "GB_${item.sha}.${item.path.substringAfterLast('.').lowercase(java.util.Locale.ROOT)}"

internal fun values(vararg pairs: Pair<String, Any?>) = ContentValues().apply {
    for ((key, value) in pairs) when (value) {
        null -> putNull(key)
        is String -> put(key, value)
        is Long -> put(key, value)
        is Int -> put(key, value)
        else -> error("Unsupported column type")
    }
}
