package dev.gallerybridge

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executors
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private val model: BridgeModel by viewModels()

    private val zipPicker = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val granted = uris.filter {
            runCatching { contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }.isSuccess
        }
        if (granted.isNotEmpty()) model.addSources(granted, uris.size - granted.size)
        else if (uris.isNotEmpty()) model.message = "NEEDS_USER_ACTION · o provedor não manteve acesso aos ZIPs."
    }
    private val localPicker = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach {
                runCatching { contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            }
            model.compareLocal(uris)
        }
    }
    private val exportPicker = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) model.export(uri)
    }
    private val catalogPicker = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) model.exportCatalogFile(uri)
    }
    private val motionPicker = registerForActivityResult(ActivityResultContracts.CreateDocument("image/jpeg")) { uri ->
        if (uri != null) model.exportMotion(uri)
    }
    private val favoriteResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        model.finishFavorites(result.resultCode == RESULT_OK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.refresh()
        setContent {
            val colors = if (androidx.compose.foundation.isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = colors) {
                Workspace(model, Actions(
                    pickZips = { zipPicker.launch(arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream")) },
                    pickLocal = { localPicker.launch(arrayOf("image/*", "video/*")) },
                    export = { exportPicker.launch("GalleryBridge-relatorio-v1.json") },
                    exportCatalog = { catalogPicker.launch("GalleryBridge-catalogo-v1.jsonl") },
                    openGallery = ::openGallery,
                    takeout = { openExternal(Intent(Intent.ACTION_VIEW, Uri.parse("https://takeout.google.com/"))) },
                    favorite = { item, desired -> model.prepareFavorite(item, desired, ::launchFavorite) },
                    favorites = { changes -> model.prepareFavorites(changes, ::launchFavorite) },
                    motion = { motionPicker.launch("GalleryBridge-Motion.jpg") }
                ))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) model.pause()
    }

    private fun openGallery() {
        val intent = packageManager.getLaunchIntentForPackage("com.sec.android.gallery3d")
        if (intent == null) model.message = "Samsung Gallery indisponível neste aparelho." else openExternal(intent)
    }

    private fun openExternal(intent: Intent) {
        try { startActivity(intent) } catch (_: ActivityNotFoundException) {
            model.message = "Nenhum aplicativo disponível para esta etapa."
        }
    }

    private fun launchFavorite(request: android.app.PendingIntent) {
        try {
            favoriteResult.launch(IntentSenderRequest.Builder(request.intentSender).build())
        } catch (_: RuntimeException) {
            model.finishFavorites(false)
        }
    }

}

internal data class Actions(
    val pickZips: () -> Unit, val pickLocal: () -> Unit, val export: () -> Unit,
    val exportCatalog: () -> Unit, val openGallery: () -> Unit, val takeout: () -> Unit,
    val favorite: (Item, Boolean) -> Unit, val favorites: (List<FavoriteChange>) -> Unit, val motion: () -> Unit
)

internal class BridgeModel(application: Application) : AndroidViewModel(application) {
    private val ledger = Ledger(application)
    private val work = Work(application, ledger)
    private val favorites = FavoriteReconciliation(application, ledger)
    private val motion = MotionExport(application, ledger)
    private val device = Device(application)
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val preferences = application.getSharedPreferences("options", 0)

    @Volatile private var pauseRequested = false
    var busy by mutableStateOf(false)
        private set
    var message by mutableStateOf("Selecione os ZIPs da sua exportação.")
    var summary by mutableStateOf(JSONObject())
        private set
    var sources by mutableStateOf(listOf<Source>())
        private set
    var page by mutableStateOf(listOf<Item>())
        private set
    var pageOffset by mutableStateOf(0)
        private set
    var snapshot by mutableStateOf<DeviceSnapshot?>(null)
        private set
    var active by mutableStateOf<Long?>(null)
        private set
    var last by mutableStateOf<Long?>(null)
        private set
    var batches by mutableStateOf<List<BatchProgress>>(emptyList())
        private set
    var pilot by mutableStateOf(false)
        private set
    var plan by mutableStateOf<List<Item>?>(null)
    var detail by mutableStateOf<Preview?>(null)
    var detailField by mutableStateOf(Field.CAPTURE_TIME)
        private set
    var favoritePlan by mutableStateOf<List<FavoriteReview>?>(null)
    var pendingConsent by mutableStateOf(false)
        private set
    private var deferredFavoriteResult: Boolean? = null
    var consent by mutableStateOf(preferences.getBoolean("gallery_consent", false))
        private set
    var expectedArchives by mutableStateOf(preferences.getString("archive_expected", "").orEmpty())
        private set
    fun setArchiveExpectation(value: String) {
        if (value.isEmpty() || value.toIntOrNull() in 1..1000) {
            expectedArchives = value
            preferences.edit().putString("archive_expected", value).apply()
        }
    }
    var reserve by mutableStateOf("2")
    var limit by mutableStateOf("20")
    var maxGiB by mutableStateOf("4")
    var minimumBattery by mutableStateOf("25")
    var onlyCharging by mutableStateOf(false)
    var diagnostics by mutableStateOf(false)
    var sdkDiagnostics by mutableStateOf<MediaSyncSnapshot?>(null)
        private set
    val galleryAvailable = runCatching {
        application.packageManager.getPackageInfo("com.sec.android.gallery3d", 0)
    }.isSuccess
    val providerVersions = listOf("com.sec.android.gallery3d", "com.samsung.android.agent.storage",
        "com.google.android.apps.photos").map { name ->
        val version = runCatching { application.packageManager.getPackageInfo(name, 0).versionName }.getOrNull()
        name to version
    }

    fun acknowledge(checked: Boolean) {
        consent = checked
        preferences.edit().putBoolean("gallery_consent", checked).apply()
    }

    fun pause() { if (busy) pauseRequested = true }

    fun refresh(offset: Int = pageOffset) = run(offset) { null }

    fun addSources(uris: List<Uri>, denied: Int) = run {
        val resolver = getApplication<Application>().contentResolver
        uris.forEach { uri ->
            val title = runCatching {
                resolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                    if (it.moveToFirst()) it.getString(0)?.take(128) else null
                }
            }.getOrNull()
            ledger.addSource(uri.toString(), title)
        }
        handler.post { plan = null }
        "${uris.size} ZIP(s) selecionado(s). ${if (denied > 0) "$denied sem acesso persistente." else ""}"
    }

    fun ingest() = withSettings { settings ->
        work.ingest(settings.reserve, gate(settings)::check, ::progress)
        "Conferência concluída. Consulte o estado de cada parte e continue se a cobertura for suficiente."
    }

    fun compareLocal(uris: List<Uri>) = withSettings { settings ->
        "${work.scanLocal(uris, gate(settings)::check)} correspondência(s) pelos bytes dos arquivos escolhidos."
    }

    fun audit() = withSettings { settings ->
        work.audit(gate(settings)::check)
        "MediaStore conferido. Ausência física fica em revisão de vínculo."
    }

    fun previewBatch() = withSettings { settings ->
        gate(settings).check()
        demand(consent && ledger.activeBatch() == null, Reason.DESTINATION_REVIEW)
        val budget = minOf(settings.maxBytes, device.state().free - settings.reserve)
        demand(budget > 0, Reason.SPACE)
        val candidates = ledger.plan(if (ledger.hasReviewedPilot()) settings.limit else minOf(settings.limit, 40), budget)
        handler.post { plan = candidates.takeIf(List<Item>::isNotEmpty) }
        if (candidates.isEmpty()) "Sem itens elegíveis dentro do limite. Confira espaço, privacidade, origem e revisão de vídeos." else null
    }

    fun applyBatch(items: List<Item>) = withSettings { settings ->
        val gate = gate(settings)
        gate.check()
        demand(consent, Reason.DESTINATION_REVIEW)
        val budget = minOf(settings.maxBytes, device.state().free - settings.reserve)
        val current = ledger.plan(if (ledger.hasReviewedPilot()) settings.limit else minOf(settings.limit, 40), budget)
        demand(current == items, Reason.DESTINATION_REVIEW)
        val batch = ledger.createBatch(items)
        work.stage(batch, settings.reserve, gate::check, ::progress)
        "Lote $batch indexado. A próxima etapa acontece na Samsung Gallery."
    }

    fun resume() = withSettings { settings ->
        demand(consent, Reason.DESTINATION_REVIEW)
        val batch = ledger.activeBatch() ?: return@withSettings "Nenhum lote aberto."
        work.stage(batch, settings.reserve, gate(settings)::check, ::progress)
        "Lote $batch indexado. A próxima etapa acontece na Samsung Gallery."
    }

    fun attest() = run {
        ledger.lastBatch()?.let(ledger::attest)
        "Revisão visual registrada. Vínculo remoto desconhecido. Gate de liberação de espaço bloqueado."
    }

    fun history() = run {
        ledger.recordHistoricalPending()
        "As dez pendências históricas estão registradas como não resolvidas."
    }

    fun inspect(item: Item, field: Field = Field.CAPTURE_TIME) = run {
        val result = retrievePreview(item)
        handler.post {
            detailField = field
            detail = result
        }
        null
    }

    private fun retrievePreview(item: Item): Preview {
        val sidecar = ledger.getSidecar(item.sha)?.let(::parseSidecar)
        val identity = item.metadata?.let(::JSONObject)?.optString("live_id")?.let(::normalizeLiveId)
        val pair = identity?.let { ledger.livePair(item.sha, it, item.mime.startsWith("video/")) }
        val sources = ledger.favoriteSourceEvidence(item.sha)
        return Preview(item, sidecar, ledger.albumPaths(item.sha), ledger.approvedVideo(item.sha),
            pair != null, ledger.excluded(item.sha), ledger.restricted(item.sha), ledger.edits(item.sha),
            canExportMotion = pair != null && motion.pair(item) != null,
            favoriteSources = sources,
            favoriteEvidence = ledger.favoriteEvidence(item.sha))
    }

    fun exclude(preview: Preview, excluded: Boolean) = run {
        ledger.exclude(preview.item.sha, excluded)
        val updated = retrievePreview(preview.item)
        handler.post { detail = updated }
        if (excluded) "Item excluído dos próximos lotes." else "Exclusão retirada para os próximos lotes."
    }

    fun approveVideo(preview: Preview) = run {
        ledger.approveVideo(preview.item.sha)
        handler.post { detail = preview.copy(approvedVideo = true) }
        "Vídeo aprovado individualmente para uma cópia independente."
    }

    fun applyDecision(preview: Preview, field: Field, action: Action, similar: List<Preview>, manual: String?) = run {
        val reviewed = similar.ifEmpty { listOf(preview) }
        var staged = 0
        for (candidate in reviewed) {
            localCheckpoint()
            val item = ledger.item(candidate.item.sha) ?: continue
            val draft = if (manual != null) validatedPreparation(item, field, manual) else
                suggestedPreparation(item, candidate.takeout, candidate.albums, field, action)
            if (draft != null) {
                ledger.saveEdit(item, draft)
                staged++
            } else if (action == Action.KEEP_DESTINATION) {
                ledger.clearEdit(item.sha, field)
            }
            ledger.recordDecision(item.sha, field, action,
                if (similar.isEmpty()) "individual_conflict" else "similar_conflicts_only")
        }
        val updated = retrievePreview(ledger.item(preview.item.sha) ?: preview.item)
        handler.post { detail = updated }
        if (staged > 0) "$staged ajuste(s) autorizado(s) para a próxima preparação. A verificação ocorre após a escrita."
        else "Decisão registrada. Este valor ou destino requer revisão manual."
    }

    fun findSimilar(preview: Preview, field: Field, onReady: (List<Preview>) -> Unit) = run {
        val key = preview.group(field)
        val result = if (key == null) emptyList() else ledger.preparationCandidates(
            preview.item.source.id, preview.item.mime
        ).mapNotNull {
            localCheckpoint()
            val candidate = retrievePreview(it)
            candidate.takeIf { !it.restricted && !it.excluded && it.group(field) == key }
        }
        handler.post { onReady(result) }
        if (result.isEmpty()) "Sem conflitos equivalentes elegíveis nesta origem." else null
    }

    fun exportMotion(destination: Uri) {
        val original = detail?.item ?: return
        withSettings { settings ->
            motion.export(original, destination, settings.reserve, gate(settings)::check)
            "Cópia Motion JPEG exportada e validada estruturalmente. Reprodução e propagação precisam de teste na Gallery."
        }
    }

    fun previewFavorites() = run {
        if (android.os.Build.VERSION.SDK_INT < 30) return@run "Favoritos locais precisam do Android 11 ou posterior."
        val entries = favorites.preview(::localCheckpoint)
        handler.post { favoritePlan = entries }
        "${entries.size} identidade(s) com indicação histórica de favorito ou conflito."
    }

    fun prepareFavorite(item: Item, desired: Boolean, launch: (android.app.PendingIntent) -> Unit) = run {
        val review = favorites.inspect(item, desired, ::localCheckpoint)
        val change = review.change ?: return@run "Favorito local · ${review.state.label()}."
        prepareFavoriteRequest(listOf(change), launch)
    }

    fun prepareFavorites(changes: List<FavoriteChange>, launch: (android.app.PendingIntent) -> Unit) = run {
        prepareFavoriteRequest(changes, launch)
    }

    private fun prepareFavoriteRequest(changes: List<FavoriteChange>, launch: (android.app.PendingIntent) -> Unit): String? {
        val request = try {
            favorites.prepare(changes, ::localCheckpoint)
        } catch (_: IllegalArgumentException) {
            return "O MediaStore não aceitou os itens selecionados. Confira o acesso e o estado local."
        } catch (_: SecurityException) {
            return "O MediaStore exige acesso novamente ou não permite a operação neste destino."
        }
        handler.post { launch(request) }
        return null
    }

    fun finishFavorites(approved: Boolean) {
        if (busy) {
            deferredFavoriteResult = approved
            return
        }
        checkFavoriteReceipts(approved)
    }

    fun recoverFavorites() = checkFavoriteReceipts(null)

    private fun checkFavoriteReceipts(approved: Boolean?) = run {
        val count = favorites.finish(approved, ::localCheckpoint)
        val selected = detail?.item?.sha?.let(ledger::item)
        selected?.let(::refreshDetail)
        handler.post { favoritePlan = null }
        "$count estado(s) confirmado(s) no MediaStore. Propagação para Google Fotos pendente de evidência."
    }

    private var nextLocalSample = 0L
    private fun localCheckpoint() {
        demand(!pauseRequested, Reason.PAUSED)
        val now = android.os.SystemClock.elapsedRealtime()
        if (now < nextLocalSample) return
        val state = device.state()
        demand(state.thermal < android.os.PowerManager.THERMAL_STATUS_SEVERE &&
            (state.headroom == null || state.headroom < 0.90f), Reason.THERMAL)
        if (state.thermal == android.os.PowerManager.THERMAL_STATUS_MODERATE) Thread.sleep(45)
        nextLocalSample = now + 500
    }

    fun inspectSdk(item: Item) = run {
        val current = ledger.item(item.sha) ?: throw BridgeFailure(Reason.SOURCE_CHANGED)
        demand(!ledger.restricted(item.sha) && !ledger.excluded(item.sha), Reason.DESTINATION_REVIEW)
        val result = MediaSyncDiagnostics(getApplication()).inspect(current, ledger.localTargets(item.sha), ::localCheckpoint)
        ledger.recordFavoriteEvidence(item.sha, FavoriteObservationLevel.SDK_CACHE, result.sdk?.name ?: "UNKNOWN")
        refreshDetail(current)
        result.status
    }

    fun observeCurrentFavorite(preview: Preview) = run {
        val item = ledger.item(preview.item.sha) ?: throw BridgeFailure(Reason.SOURCE_CHANGED)
        val flag = favorites.currentFlag(item, ::localCheckpoint)
        val state = when (flag) {
            true -> "FAVORITED"
            false -> "NOT_FAVORITED"
            null -> "UNKNOWN"
        }
        ledger.recordFavoriteEvidence(item.sha, FavoriteObservationLevel.LOCAL_STATE, state)
        refreshDetail(item)
        when (flag) {
            true -> "Favorito presente no MediaStore."
            false -> "Favorito ausente no MediaStore."
            null -> "O MediaStore não forneceu estado para uma identidade local única."
        }
    }

    fun recordRemoteFavorite(preview: Preview, seen: Boolean) = run {
        val item = ledger.item(preview.item.sha) ?: throw BridgeFailure(Reason.SOURCE_CHANGED)
        val current = retrievePreview(item)
        demand(current.photosKey != null && current.photosKey == preview.photosKey &&
            !current.restricted && !current.excluded, Reason.DESTINATION_REVIEW)
        ledger.recordFavoriteEvidence(item.sha, FavoriteObservationLevel.REMOTE_VISUAL,
            if (seen) "FAVORITED" else "NOT_FAVORITED")
        refreshDetail(item)
        "Observação manual registrada. Verificação remota independente pendente."
    }

    private fun refreshDetail(item: Item) {
        val updated = retrievePreview(item)
        handler.post { if (detail?.item?.sha == item.sha) detail = updated }
    }

    fun readSdkDiagnostics() = run {
        val result = MediaSyncDiagnostics(getApplication()).read()
        handler.post { sdkDiagnostics = result }
        result.status
    }

    fun export(uri: Uri) = run {
        val report = ledger.summary().put("observed_at", java.time.Instant.now().toString())
        if (diagnostics) report.put("helper_diagnostics", snapshot?.let {
            JSONObject().put("uid_rx_total", it.helperRx).put("uid_tx_total", it.helperTx)
                .put("thermal_status", it.thermal).put("wifi_unmetered", it.wifi)
                .put("native_sync_state", "UNKNOWN")
        })
        if (diagnostics) sdkDiagnostics?.let {
            report.put("sdk_aggregate_evidence", JSONObject().put("association_signals", it.associated)
                .put("commit_count", it.queue).put("commits_with_retry", it.retried)
                .put("backup_rows", it.backup))
        }
        val destination = getApplication<Application>().contentResolver.openOutputStream(uri, "wt")
            ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        destination.bufferedWriter().use { it.write(report.toString(2)) }
        "Relatório sanitizado exportado."
    }

    fun exportCatalogFile(uri: Uri) = run {
        val destination = getApplication<Application>().contentResolver.openOutputStream(uri, "wt")
            ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        destination.bufferedWriter().use { writer ->
            exportCatalog(ledger.readableDatabase, writer) { demand(!pauseRequested, Reason.PAUSED) }
        }
        "Catálogo v1 exportado. A última linha registra o fechamento."
    }

    private data class Settings(val reserve: Long, val maxBytes: Long, val limit: Int, val battery: Int, val charging: Boolean)

    private fun settings(): Settings? {
        val reserveGiB = reserve.toIntOrNull()?.takeIf { it in 1..100 } ?: return null
        val size = maxGiB.toIntOrNull()?.takeIf { it in 1..80 } ?: return null
        val count = limit.toIntOrNull()?.takeIf { it in 1..25_000 } ?: return null
        val battery = minimumBattery.toIntOrNull()?.takeIf { it in 15..100 } ?: return null
        return Settings(reserveGiB * GIB, size * GIB, count, battery, onlyCharging)
    }

    private fun withSettings(task: (Settings) -> String?) {
        val settings = settings()
        if (settings == null) {
            message = "Confira limites. Reserva 1–100 GiB, lote 1–80 GiB, 1–25.000 arquivos, bateria 15–100%."
            return
        }
        run { task(settings) }
    }

    private fun gate(config: Settings) = WorkGate(device, config.battery, config.charging, { pauseRequested }, config.reserve)

    private fun progress(text: String) { handler.post { message = text } }

    private fun run(offset: Int = pageOffset, task: () -> String?) {
        if (busy) return
        busy = true
        pauseRequested = false
        executor.execute {
            val status = try { task() } catch (error: BridgeFailure) {
                "${error.reason.name} · consulte os limites ou a fonte e retome."
            } catch (_: SecurityException) {
                "NEEDS_USER_ACTION · o acesso do Android foi revogado. Selecione novamente o documento."
            } catch (_: java.io.IOException) {
                "SOURCE_INCOMPLETE · erro de leitura ou gravação. Confira a origem e o espaço."
            } catch (_: RuntimeException) {
                "REVIEW · falha do provedor ou do banco. O checkpoint existente foi preservado."
            }
            val data = runCatching {
                ScreenData(ledger.summary(), ledger.sources(), ledger.page(offset),
                    device.state(), ledger.activeBatch(), ledger.lastBatch(), ledger.hasReviewedPilot(),
                    ledger.pendingFavorites().isNotEmpty(), ledger.batchProgress())
            }.getOrNull()
            handler.post {
                if (status != null) message = status
                if (data != null) {
                    summary = data.summary
                    sources = data.sources
                    page = data.page
                    pageOffset = offset
                    snapshot = data.snapshot
                    active = data.active
                    last = data.last
                    pilot = data.pilot
                    pendingConsent = data.pendingConsent
                    batches = data.batches
                }
                busy = false
                deferredFavoriteResult?.let {
                    deferredFavoriteResult = null
                    finishFavorites(it)
                }
            }
        }
    }

    override fun onCleared() {
        pauseRequested = true
        executor.submit { ledger.close() }
        executor.shutdown()
    }

    private data class ScreenData(
        val summary: JSONObject, val sources: List<Source>, val page: List<Item>, val snapshot: DeviceSnapshot,
        val active: Long?, val last: Long?, val pilot: Boolean, val pendingConsent: Boolean, val batches: List<BatchProgress>
    )
}
