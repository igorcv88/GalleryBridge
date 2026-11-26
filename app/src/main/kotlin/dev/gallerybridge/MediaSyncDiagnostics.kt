package dev.gallerybridge

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.util.concurrent.TimeUnit

internal data class MediaSyncSnapshot(
    val status: String, val associated: Long? = null, val queue: Long? = null,
    val retried: Long? = null, val backup: Long? = null
)

internal data class MediaSyncItemObservation(val status: String, val sdk: SdkFavorite? = null)

internal class MediaSyncDiagnostics(private val context: Context) {
    fun read(): MediaSyncSnapshot {
        val (state, counts) = readCounts(QUERY)
        if (counts == null) return MediaSyncSnapshot(state)
        return MediaSyncSnapshot("Indicadores agregados do agente", counts[0], counts[1], counts[2], counts[3])
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

    @Suppress("SwallowedException")
    private fun readCounts(query: String): Pair<String, List<Long>?> {
        val version = runCatching {
            val info = context.packageManager.getPackageInfo(PACKAGE, 0)
            info.versionName.takeIf { info.longVersionCode == PROFILE_CODE }
        }.getOrNull()
        if (version != PROFILE) return "Perfil do agente indisponível ou versão fora do perfil." to null
        // Only numeric counters cross the process boundary. The SQL statement is one quoted shell argument.
        val sql = "'" + query.replace("'", "'\"'\"'") + "'"
        val command = """
            db='$DATABASE'
            [ -r "${'$'}db" ] && [ -f "${'$'}db" ] && [ ! -L "${'$'}db" ] || exit 31
            [ "${'$'}(readlink -f "${'$'}db")" = "${'$'}db" ] || exit 31
            [ -x /system/bin/sqlite3 ] || exit 32
            exec /system/bin/sqlite3 -readonly -batch -bail "${'$'}db" $sql 2>/dev/null
        """.trimIndent()
        val process = try { ProcessBuilder("su", "-c", command).redirectErrorStream(true).start() }
        catch (_: java.io.IOException) { return "Root indisponível." to null }
        return try {
            if (!process.waitFor(25, TimeUnit.SECONDS)) return "Tempo de autorização excedido." to null
            if (process.exitValue() != 0) return when (process.exitValue()) {
                31 -> "Banco do perfil não acessível ou caminho diferente."
                32 -> "O dispositivo não oferece /system/bin/sqlite3 para este perfil."
                else -> "Leitura recusada ou esquema do MediaSync incompatível."
            } to null
            val counts = process.inputStream.use { stream ->
                val bytes = ByteArray(128)
                var size = 0
                while (size < bytes.size) {
                    val read = stream.read(bytes, size, bytes.size - size)
                    if (read <= 0) break
                    size += read
                }
                if (size in 1 until bytes.size)
                    String(bytes, 0, size, Charsets.UTF_8).trim().split('|').map { it.toLongOrNull() ?: -1L }
                else emptyList()
            }
            if (counts.size != 4 || counts.any { it < 0 }) "Resposta diagnóstica fora do formato." to null
            else "Consulta concluída." to counts
        } finally {
            process.destroyForcibly()
        }
    }

    companion object {
        private const val PACKAGE = "com.samsung.android.agent.storage"
        private const val PROFILE = "1.6.09.34"
        private const val PROFILE_CODE = 0x099723c2L
        private const val DATABASE =
            "/data/user/0/com.samsung.android.agent.storage/files/backup_storage/1/" +
                "com.google.android.libraries.mediasync.impl.database/backup_storage_db"
        private const val QUERY = "PRAGMA query_only=ON;" +
            "SELECT (SELECT count(*) FROM synced_folder_media_metadata WHERE upload_status=0 AND is_dirty=0 " +
            "AND length(cloud_hash)>0 AND length(folder_media_id)>0)," +
            "(SELECT count(*) FROM commit_queue)," +
            "(SELECT count(*) FROM commit_queue WHERE retry_count>0)," +
            "(SELECT count(*) FROM backup_status);"
    }
}
