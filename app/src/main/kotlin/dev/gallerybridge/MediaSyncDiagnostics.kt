package dev.gallerybridge

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.util.concurrent.TimeUnit

internal data class MediaSyncSnapshot(
    val status: String, val associated: Long? = null, val queue: Long? = null,
    val retried: Long? = null, val backup: Long? = null,
    val sample: SyncSample? = null
)

internal data class MediaSyncItemObservation(val status: String, val sdk: SdkFavorite? = null)

internal class MediaSyncDiagnostics(private val context: Context) {
    fun read(): MediaSyncSnapshot {
        val now = System.currentTimeMillis()
        val (status, rows) = readLines(healthQuery(now), health = true)
        val counters = rows?.firstOrNull()?.let { numeric(it, 7) }
            ?: return MediaSyncSnapshot(status)
        val commits = counters[1]
        val success = rows.drop(1).singleOrNull { it.startsWith("TIME|") }?.substringAfter('|')
            ?.toLongOrNull()?.takeIf { it in 1..now }
        val event = rows.drop(1).lastOrNull { it.startsWith("CYCLE|") }?.split('|')
        val timestamp = event?.getOrNull(1)?.toLongOrNull()?.times(1000)
        val cycle = event?.getOrNull(2)?.let { name -> SyncCycle.entries.firstOrNull { it.name == name } }
            ?: SyncCycle.UNKNOWN
        val retries = rows.drop(1).filter { it.startsWith("JOB|") }.mapNotNull {
            numeric(it.substringAfter('|'), 4)?.let { n -> SyncRetry(n[0], n[1], n[2], n[3]) }
        }.takeIf { commits in 1..16 && it.size.toLong() == commits }
        val sample = SyncSample(now, commits, counters[4], counters[5], success, cycle, timestamp, retries)
        return MediaSyncSnapshot(status, counters[0], commits, counters[2], counters[3], sample)
    }

    private fun numeric(text: String, columns: Int): List<Long>? {
        val values = text.split('|').map { it.toLongOrNull() ?: -1L }
        return values.takeIf { it.size == columns && it.all { number -> number >= 0 } }
    }

    fun inspect(item: Item, localTargets: List<String>, checkpoint: () -> Unit): MediaSyncItemObservation {
        val uri = favoriteTargets(item, localTargets).singleOrNull()?.let(Uri::parse)
            ?: return MediaSyncItemObservation("A consulta exige uma única mídia local identificada.")
        val expected = buildSet {
            add(item.sha to item.size)
            val outputSha = item.outputSha
            val outputSize = item.outputSize
            if (outputSha != null && outputSize != null) add(outputSha to outputSize)
        }
        return inspectExact(uri, expected, checkpoint)
    }

    @Suppress("SwallowedException")
    fun inspectExact(uri: Uri, expected: Set<Pair<String, Long>>, checkpoint: () -> Unit): MediaSyncItemObservation {
        if (expected.isEmpty()) return MediaSyncItemObservation("Identidade local indisponível.")
        if (android.os.Build.VERSION.SDK_INT < 30 || !isMediaItem(uri) || uri.pathSegments.first() != MediaStore.VOLUME_EXTERNAL_PRIMARY)
            return MediaSyncItemObservation("O perfil exige uma mídia do armazenamento principal.")
        val resolver = context.contentResolver
        val row = try {
            resolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.MediaColumns.IS_PENDING,
                MediaStore.MediaColumns.IS_TRASHED), null, null, null)?.use {
                if (it.moveToFirst() && it.getInt(2) == 0 && it.getInt(3) == 0) it.getString(0) to it.getString(1) else null
            }
        } catch (_: RuntimeException) { null } ?: return MediaSyncItemObservation("Linha local indisponível.")
        val (name, directory) = row
        if (name.isNullOrBlank() || directory.isNullOrEmpty() ||
            (name + directory).any { it.isISOControl() || it == '\\' } || '/' in name ||
            directory.trimEnd('/').split('/').any { it.isBlank() || it == "." || it == ".." }) {
            return MediaSyncItemObservation("Caminho local fora do perfil.")
        }
        val bytes = try {
            resolver.openInputStream(uri)?.use { digest(it, maxBytes = expected.maxOf { it.second }, checkpoint = checkpoint) }
        } catch (failure: BridgeFailure) {
            if (failure.reason != Reason.INTEGRITY) throw failure
            null
        } catch (_: java.io.IOException) { null } catch (_: SecurityException) { null }
        if (bytes == null || (bytes.sha to bytes.bytes) !in expected) return MediaSyncItemObservation("Bytes locais divergentes.")
        val filePath = (directory.trimEnd('/') + "/" + name).replace("'", "''")
        val id = requireNotNull(uri.lastPathSegment).toLong()
        val sql = "PRAGMA query_only=ON;" +
            "SELECT count(*),COALESCE(sum(CASE WHEN upload_status=0 AND is_dirty=0 AND length(cloud_hash)>0 " +
            "AND length(folder_media_id)>0 AND COALESCE(trash_state,0)=0 " +
            "AND COALESCE(permanently_missing_in_mediastore,0)=0 THEN 1 ELSE 0 END),0)," +
            "COALESCE(sum(CASE WHEN is_favorite=1 THEN 1 ELSE 0 END),0)," +
            "COALESCE(sum(CASE WHEN is_favorite=0 THEN 1 ELSE 0 END),0) " +
            "FROM synced_folder_media_metadata WHERE media_store_id=$id AND file_path IN " +
            "('/storage/emulated/0/$filePath','/sdcard/$filePath');"
        val (state, evidence) = readCounts(sql)
        if (evidence == null) return MediaSyncItemObservation(state)
        val sdk = classifySdkFavorite(evidence[0], evidence[1], evidence[2], evidence[3])
        val label = when (sdk) {
            SdkFavorite.NO_RECORD -> "Nenhuma associação encontrada no SDK."
            SdkFavorite.AMBIGUOUS -> "Mais de uma associação no SDK. Revisão necessária."
            SdkFavorite.DIRTY -> "SDK reconhece o item. Commit limpo pendente."
            SdkFavorite.CLEAN_FAVORITE -> "Associação limpa no SDK e item favorito."
            SdkFavorite.CLEAN_NOT_FAVORITE -> "Associação limpa no SDK e item sem favorito."
            SdkFavorite.CLEAN_UNKNOWN -> "Associação limpa no SDK e favorito desconhecido."
        }
        return MediaSyncItemObservation(label, sdk)
    }

    private fun readCounts(query: String): Pair<String, List<Long>?> {
        val (status, lines) = readLines(query)
        return status to lines?.singleOrNull()?.let { numeric(it, 4) }
    }

    @Suppress("SwallowedException")
    private fun readLines(query: String, health: Boolean = false): Pair<String, List<String>?> {
        val version = runCatching {
            val info = context.packageManager.getPackageInfo(PACKAGE, 0)
            info.versionName.takeIf { info.longVersionCode == PROFILE_CODE }
        }.getOrNull()
        if (version != PROFILE || android.os.Process.myUid() >= 100_000)
            return "Perfil do agente indisponível ou versão fora do perfil." to null
        val sql = "'" + query.replace("'", "'\"'\"'") + "'"
        val signals = if (health) """
            p='/data/user/0/com.samsung.android.agent.storage/shared_prefs/sync_state.xml'
            [ ! -L "${'$'}p" ] && [ -f "${'$'}p" ] &&
              /system/bin/sed -n 's/.*<long name="last_successful_sync_time" value="\([0-9][0-9]*\)".*/TIME|\1/p' "${'$'}p" 2>/dev/null
            set -- ${'$'}(/system/bin/pidof '$PACKAGE' 2>/dev/null)
            [ "${'$'}#" = 1 ] || exit 0
            pid="${'$'}1"
            now=${'$'}(/system/bin/date +%s)
            age=${'$'}(/system/bin/ps -p "${'$'}pid" -o ELAPSED= | /system/bin/awk '
              NF == 1 && ${'$'}1 ~ /^[0-9]+:[0-9][0-9](:[0-9][0-9])?$/ {
                n=split(${'$'}1, t, ":"); seconds=0
                for (i=1;i<=n;i++) seconds=seconds*60+t[i]
                print seconds
              }')
            case "${'$'}age" in ''|*[!0-9]*) exit 0;; esac
            since=${'$'}((now-age+2))
            /system/bin/logcat -d -t 1200 --pid="${'$'}pid" -v epoch SyncStatusChangeNotifier:D MS_UnifiedRemoteSyncRunner:D '*:S' 2>/dev/null |
              /system/bin/awk -v since="${'$'}since" '
                int(${'$'}1) < since { next }
                /state=PAUSED reason=TRANSIENT_ERROR|systemState=PAUSED, pausedReason=TRANSIENT_ERROR/ { print "CYCLE|" int(${'$'}1) "|PAUSED_TRANSIENT"; next }
                /state=IN_PROGRESS|systemState=IN_PROGRESS/ { print "CYCLE|" int(${'$'}1) "|ACTIVE"; next }
                /state=COMPLETED reason=null|systemState=COMPLETED, pausedReason=null/ { print "CYCLE|" int(${'$'}1) "|COMPLETED" }
              ' | /system/bin/tail -1
        """.trimIndent() else ""
        val command = """
            n=0
            for path in '/data/user/0/com.samsung.android.agent.storage/files/backup_storage'/[0-9]*/$DATABASE_SUFFIX; do
              [ -r "${'$'}path" ] && [ -f "${'$'}path" ] && [ ! -L "${'$'}path" ] || continue
              [ "${'$'}(readlink -f "${'$'}path")" = "${'$'}path" ] || exit 31
              n=${'$'}((n+1))
              db="${'$'}path"
            done
            [ "${'$'}n" = 1 ] || exit 31
            [ -x /system/bin/sqlite3 ] || exit 32
            /system/bin/sqlite3 -readonly -batch -bail -cmd '.timeout 1500' "${'$'}db" $sql 2>/dev/null || exit 33
            $signals
            exit 0
        """.trimIndent()
        val process = try { ProcessBuilder("su", "-c", command).redirectErrorStream(true).start() }
        catch (_: java.io.IOException) { return "Root indisponível." to null }
        return try {
            if (!process.waitFor(25, TimeUnit.SECONDS)) return "Tempo de autorização excedido." to null
            if (process.exitValue() != 0) return when (process.exitValue()) {
                31 -> "O diagnóstico exige um único banco legível do perfil."
                32 -> "O dispositivo não oferece /system/bin/sqlite3 para este perfil."
                else -> "Banco ocupado, leitura recusada ou esquema incompatível."
            } to null
            val lines = process.inputStream.use { stream ->
                val data = ByteArray(4097)
                var count = 0
                while (count < data.size) {
                    val read = stream.read(data, count, data.size - count)
                    if (read <= 0) break
                    count += read
                }
                if (count in 1 until data.size) String(data, 0, count, Charsets.UTF_8).trim().lines()
                else emptyList()
            }
            if (lines.isEmpty()) "Resposta diagnóstica fora do formato." to null
            else "Observação do agente Samsung." to lines
        } finally {
            process.destroyForcibly()
        }
    }

    private fun healthQuery(now: Long) = """
        PRAGMA query_only=ON;
        BEGIN;
        SELECT (SELECT count(*) FROM synced_folder_media_metadata WHERE upload_status=0 AND is_dirty=0
          AND length(cloud_hash)>0 AND length(folder_media_id)>0),
          (SELECT count(*) FROM commit_queue),
          (SELECT count(*) FROM commit_queue WHERE retry_count>0),
          (SELECT count(*) FROM backup_status),
          (SELECT count(*) FROM commit_queue WHERE schedule_timestamp>$now),
          (SELECT count(*) FROM synced_folder_media_metadata WHERE is_dirty=1),
          (SELECT count(*) FROM commit_queue WHERE schedule_timestamp IS NULL);
        SELECT 'JOB', id, entity_id, commit_generation, retry_count FROM commit_queue
          WHERE (SELECT count(*) FROM commit_queue)<=16 ORDER BY id LIMIT 16;
        COMMIT;
    """.trimIndent()

    companion object {
        private const val PACKAGE = "com.samsung.android.agent.storage"
        private const val PROFILE = "1.6.09.34"
        private const val PROFILE_CODE = 0x099723c2L
        private const val DATABASE_SUFFIX = "com.google.android.libraries.mediasync.impl.database/backup_storage_db"
    }
}
