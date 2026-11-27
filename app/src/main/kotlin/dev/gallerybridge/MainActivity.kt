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
    private val duplicatePicker = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) model.previewDuplicates(uris.map(::keepReadGrant))
    }
    private val duplicateConsent = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        model.finishDuplicateMoves(result.resultCode == RESULT_OK)
    }
    private val datePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) model.previewDate(keepReadGrant(uri))
    }
    private val dateOutput = registerForActivityResult(ActivityResultContracts.CreateDocument("image/jpeg")) { uri ->
        if (uri != null) model.exportDateCopy(uri)
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
    private val survivorPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) model.selectMotionSurvivor(keepReadGrant(uri))
    }
    private val redundantPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) model.compareMotion(keepReadGrant(uri))
    }
    private val trashResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        model.finishMotionOperation(result.resultCode == RESULT_OK)
    }

    private fun keepReadGrant(uri: Uri): Uri {
        runCatching { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        return uri
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
                    motion = { motionPicker.launch("GalleryBridge-Motion.jpg") },
                    selectDuplicates = { duplicatePicker.launch(arrayOf("image/*")) },
                    approveDuplicates = { request ->
                        try { duplicateConsent.launch(IntentSenderRequest.Builder(request.intentSender).build()) }
                        catch (_: RuntimeException) { model.finishDuplicateMoves(false) }
                    },
                    selectDate = { datePicker.launch(arrayOf("image/*")) },
                    saveDate = { dateOutput.launch("GalleryBridge-data-revisada.jpg") },
                    motionActions = MotionActions(
                        { survivorPicker.launch(arrayOf("image/*")) },
                        { redundantPicker.launch(arrayOf("image/*", "video/*")) },
                        ::launchMotionRequest,
                        { uri -> openExternal(Intent(Intent.ACTION_VIEW, uri)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) }
                    )
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

    private fun launchMotionRequest(request: android.app.PendingIntent) {
        try {
            trashResult.launch(IntentSenderRequest.Builder(request.intentSender).build())
        } catch (_: RuntimeException) {
            model.finishMotionOperation(false)
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
    val favorite: (Item, Boolean) -> Unit, val favorites: (List<FavoriteChange>) -> Unit, val motion: () -> Unit,
    val selectDuplicates: () -> Unit, val approveDuplicates: (android.app.PendingIntent) -> Unit,
    val selectDate: () -> Unit, val saveDate: () -> Unit,
    val motionActions: MotionActions
)

internal class BridgeModel(application: Application) : AndroidViewModel(application) {
    private val ledger = Ledger(application)
    private val work = Work(application, ledger)
    private val favorites = FavoriteReconciliation(application, ledger)
    private val motion = MotionExport(application, ledger)
    private val motionNative = MotionNative(application, ledger, motion)
    private val duplicates = DuplicateReview(application, ledger, motionNative)
    private val dateRepair = DateRepair(application, ledger)
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
    var nativeHealth by mutableStateOf<SyncAssessment?>(null)
        private set
    private var previousSyncSample: SyncSample? = null
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

    var motionInventory by mutableStateOf<List<MotionInventoryEntry>>(emptyList())
        private set
    var motionOffset by mutableStateOf(0)
        private set
    var motionSurvivor by mutableStateOf<Uri?>(null)
        private set
    private var knownHistoricalSha: String? = null
    var motionComparison by mutableStateOf<MotionComparison?>(null)
        private set
    var motionOperation by mutableStateOf<MotionOperation?>(null)
        private set
    var lastMotionUri by mutableStateOf<Uri?>(null)
        private set
    private var deferredMotionResult: Boolean? = null

    fun inventoryMotion(offset: Int = motionOffset) = run {
        readMotionInventory(offset)
        null
    }

    private fun readMotionInventory(offset: Int) {
        val rows = ledger.motionGroups(24, offset).mapNotNull { members ->
            localCheckpoint()
            val items = members.mapNotNull { ledger.item(it.sha) }
            val photos = items.filter { !it.mime.startsWith("video/") }
            val photo = photos.singleOrNull()
            val video = photo?.let(motion::pair)
            val key = if (photo != null && video != null) motion.key(photo, video) else null
            val label = when {
                photo == null && photos.isEmpty() -> "Vídeo sem imagem identificada"
                photo == null || members.size > 2 -> "Par ambíguo"
                motion.stillMime(photo) != "image/jpeg" -> "Formato sem recomposição nesta versão"
                video == null -> "Par incompleto ou protegido"
                ledger.motionPublication(requireNotNull(key))?.second == "INDEXED_LOCAL" -> "Indexada localmente"
                photo.sha == knownHistoricalSha -> "Motion Photo equivalente selecionada"
                motion.staged(photo, video) -> "Recomposta e pronta para publicar"
                else -> "Par confirmado"
            }
            val representative = photo ?: items.firstOrNull() ?: return@mapNotNull null
            MotionInventoryEntry(representative, video, label)
        }.distinctBy { it.photo.sha }
        val journal = ledger.motionOperation()
        handler.post {
            motionInventory = rows
            motionOffset = offset
            motionOperation = journal
        }
    }

    fun composeMotionPage() = withSettings { settings ->
        val tasks = motionInventory.filter { it.status == "Par confirmado" }
            .take(minOf(settings.limit, 24)).map { it.photo }.iterator()
        val partition = (getApplication<Application>().filesDir.usableSpace - settings.reserve) / 2
        demand(partition > 0, Reason.SPACE)
        var complete = 0
        var failures = 0
        val dispatchGate = gate(settings)
        runMotionQueue(tasks, 2, mayDispatch = {
            if (pauseRequested) false else {
                dispatchGate.check()
                true
            }
        }, process = { photo ->
            try {
                motion.stage(photo, settings.reserve + partition, gate(settings)::check)
                null
            } catch (failure: BridgeFailure) {
                failure.reason
            } catch (_: java.io.IOException) {
                Reason.INTEGRITY
            } catch (_: SecurityException) {
                Reason.NEEDS_USER_ACTION
            }
        }, completed = { reason ->
            if (reason == null) complete++ else failures++
            progress("Motion Photos · $complete prontas, $failures em revisão" +
                (reason?.let { " · ${it.name}" } ?: ""))
        })
        readMotionInventory(motionOffset)
        "$complete recomposições preparadas. $failures pendentes. A publicação é uma etapa separada."
    }

    fun publishMotion(photo: Item) = withSettings { settings ->
        val uri = motionNative.publish(photo, settings.reserve, gate(settings)::check)
        readMotionInventory(motionOffset)
        handler.post { lastMotionUri = uri }
        "Cópia indexada localmente. Confira a reprodução na Galeria antes de sincronizar."
    }

    fun selectMotionSurvivor(uri: Uri) = run {
        val canonical = motionNative.canonical(uri)
        val exact = motionNative.matchingOriginal(canonical, ::localCheckpoint)
        knownHistoricalSha = exact?.sha
        readMotionInventory(motionOffset)
        handler.post { motionSurvivor = canonical; motionComparison = null }
        if (exact == null) "Selecione agora o possível redundante."
        else "Motion Photo selecionada com os mesmos componentes do ZIP. O par fica fora da recomposição nesta revisão."
    }

    fun comparisonDismiss() { motionComparison = null }

    fun compareMotion(uri: Uri) = run {
        val survivor = motionSurvivor ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        val candidate = motionNative.canonical(uri)
        val plan = motionNative.compare(survivor, candidate, ::localCheckpoint)
        handler.post { motionComparison = plan }
        plan.reason
    }

    fun observeMotionLink(plan: MotionComparison) = run {
        demand(diagnostics, Reason.NEEDS_USER_ACTION)
        val item = plan.survivor
        val observation = MediaSyncDiagnostics(getApplication()).inspectExact(
            item.uri, setOf(item.sha to item.bytes), ::localCheckpoint
        )
        "MediaSync: ${observation.status} Reprodução e conta remota permanecem separadas."
    }

    fun moveMotion(plan: MotionComparison, attestation: MotionAttestation) = withSettings { settings ->
        val state = motionNative.moveForReview(plan, attestation, settings.reserve, gate(settings)::check)
        handler.post { motionOperation = state; motionComparison = null }
        "Movimento local: ${state.state}. Confira a ligação na Galeria Samsung."
    }

    fun trashMotion(plan: MotionComparison, attestation: MotionAttestation,
        launch: (android.app.PendingIntent) -> Unit) = withSettings { settings ->
        val request = motionNative.requestTrash(plan, attestation, settings.reserve, gate(settings)::check)
        val journal = ledger.motionOperation()
        handler.post { motionOperation = journal; motionComparison = null; launch(request) }
        null
    }

    fun undoMotionMove() = withSettings { settings ->
        val result = motionNative.undoMove(gate(settings)::check)
        handler.post { motionOperation = result; motionComparison = null }
        "Reversão local: ${result.state}. Efeito remoto não verificado."
    }

    fun restoreMotion(launch: (android.app.PendingIntent) -> Unit) = run {
        val request = motionNative.requestRestore(::localCheckpoint)
        val journal = ledger.motionOperation()
        handler.post { motionOperation = journal; launch(request) }
        null
    }

    fun finishMotionOperation(approved: Boolean?) {
        if (busy) {
            if (approved != null) deferredMotionResult = approved
            return
        }
        run {
            val state = motionNative.finish(approved, ::localCheckpoint)
            handler.post { motionOperation = state }
            state?.let { "Resultado local: ${it.state}. Efeito remoto não verificado." }
        }
    }

    var duplicatePreview by mutableStateOf<DuplicatePreview?>(null)
        private set
    var duplicateSummary by mutableStateOf<Map<String, Int>>(emptyMap())
        private set
    private var deferredDuplicateResult: Boolean? = null

    fun previewDuplicates(selected: List<Uri>) = run { reviewDuplicates(selected) }

    fun previewCatalogDuplicates() = run {
        reviewDuplicates(ledger.duplicateCatalogTargets().map(Uri::parse))
    }

    private fun reviewDuplicates(selected: List<Uri>): String {
        val result = duplicates.preview(selected, ::localCheckpoint)
        var allowance = 48
        val visible = result.groups.mapNotNull { group ->
            val targets = group.candidates.take(allowance)
            allowance -= targets.size
            group.copy(candidates = targets).takeIf { targets.isNotEmpty() }
        }
        handler.post { duplicatePreview = DuplicatePreview(visible, result.held) }
        return "${result.candidates.size} redundantes exatos na seleção. Até 48 por aprovação."
    }

    fun requestDuplicateMoves(plan: DuplicatePreview, launch: (android.app.PendingIntent) -> Unit) = run {
        val request = duplicates.request(plan, ::localCheckpoint)
        handler.post { duplicatePreview = null; launch(request) }
        null
    }

    fun finishDuplicateMoves(approved: Boolean?) {
        if (busy) {
            if (approved != null) deferredDuplicateResult = approved
            return
        }
        withSettings { settings ->
            try {
                val checkpoint = if (approved == true) gate(settings)::check else ::localCheckpoint
                duplicates.finish(approved, checkpoint)
                "Verificação local concluída. Efeito remoto desconhecido."
            } finally {
                val summary = ledger.duplicateJobs().groupingBy { it.state }.eachCount()
                handler.post { duplicateSummary = summary }
            }
        }
    }

    var datePreview by mutableStateOf<DateReview?>(null)
        private set
    var dateProposal by mutableStateOf<DateCopyPlan?>(null)
        private set

    fun previewDate(uri: Uri) = run {
        val result = dateRepair.preview(uri, ::localCheckpoint)
        handler.post { datePreview = result; dateProposal = null }
        null
    }

    fun proposeDate(input: String) = run {
        val source = datePreview ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        val plan = dateRepair.plan(source, input, ::localCheckpoint)
        handler.post { dateProposal = plan }
        "Prévia verificada. Escolha onde salvar a cópia."
    }

    fun exportDateCopy(destination: Uri) = run {
        val plan = dateProposal ?: throw BridgeFailure(Reason.NEEDS_USER_ACTION)
        dateRepair.export(plan, destination, ::localCheckpoint)
        handler.post { dateProposal = null }
        "Cópia verificada. O original permanece intacto. O estado na nuvem continua desconhecido."
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
    @Synchronized
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
        val sample = result.sample
        val assessment = sample?.let { assessSync(previousSyncSample, it) }
        previousSyncSample = sample
        handler.post { sdkDiagnostics = result; nativeHealth = assessment }
        result.status
    }

    fun export(uri: Uri) = run {
        val report = ledger.summary().put("observed_at", java.time.Instant.now().toString())
        val moves = ledger.duplicateJobs().groupingBy { it.state }.eachCount()
        if (moves.isNotEmpty()) report.put("duplicate_review", JSONObject(moves)).put("duplicate_remote_verified", false)
        ledger.motionOperation()?.let { op ->
            report.put("motion_review", JSONObject().put("plan_digest", op.digest).put("state", op.state)
                .put("remote_server_verified", false).put("delete_authorized", false))
        }
        if (diagnostics) report.put("helper_diagnostics", snapshot?.let {
            JSONObject().put("uid_rx_total", it.helperRx).put("uid_tx_total", it.helperTx)
                .put("thermal_status", it.thermal).put("wifi_unmetered", it.wifi)
                .put("native_sync_state", "UNKNOWN")
        })
        if (diagnostics) sdkDiagnostics?.let {
            report.put("sdk_aggregate_evidence", JSONObject().put("association_signals", it.associated)
                .put("commit_count", it.queue).put("commits_with_retry", it.retried)
                .put("backup_rows", it.backup).put("health", nativeHealth?.health?.name)
                .put("commits_per_minute", nativeHealth?.commitsPerMinute)
                .put("dirty_rows", it.sample?.dirty).put("scheduled_commits", it.sample?.future)
                .put("observed_success_ms", it.sample?.lastSuccess))
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
                deferredMotionResult?.let {
                    deferredMotionResult = null
                    finishMotionOperation(it)
                }
                deferredDuplicateResult?.let {
                    deferredDuplicateResult = null
                    finishDuplicateMoves(it)
                }
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
