package dev.gallerybridge

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.util.Locale
import org.json.JSONObject

private enum class Screen(val label: String) { SOURCES("Origem"), BATCH("Lotes"), METADATA("Metadados"), REPORT("Relatório"), DIAGNOSTICS("Avançado") }

@Composable
internal fun Workspace(model: BridgeModel, actions: Actions) {
    var screen by remember { mutableStateOf(Screen.SOURCES) }
    var reviewed by remember { mutableStateOf(false) }
    var historical by remember { mutableStateOf(false) }
    var exportCatalog by remember { mutableStateOf(false) }
    var rootConsent by remember { mutableStateOf(false) }
    val summary = model.summary
    Scaffold { inset ->
        Column(Modifier.padding(inset).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceBetween) {
                Screen.entries.forEach {
                    TextButton(onClick = { screen = it }, enabled = screen != it) { Text(it.label) }
                }
            }
            Text(model.message, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)
            if (model.busy) OutlinedButton(onClick = model::pause) { Text("Pausar processamento") }
            HorizontalDivider()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                when (screen) {
                    Screen.SOURCES -> {
                        item {
                            Text("Google Takeout", style = MaterialTheme.typography.titleLarge)
                            Text("No Google Takeout, selecione Google Fotos, escolha os álbuns e exporte em partes ZIP. Baixe todas as partes ou abra as que estiverem no Drive pelo seletor do Android.")
                            TextButton(onClick = actions.takeout, enabled = !model.busy) { Text("Abrir Takeout oficial") }
                            Button(onClick = actions.pickZips, enabled = !model.busy) { Text("Selecionar ZIPs locais ou Drive") }
                            OutlinedButton(onClick = model::ingest, enabled = !model.busy && model.sources.isNotEmpty()) {
                                Text("Conferir ZIPs e retomar análise")
                            }
                            Text("${summary.optLong("archives_verified")} partes verificadas · ${summary.optLong("media_unique")} mídias únicas")
                            val duplicate = summary.optLong("media_entries") - summary.optLong("media_unique")
                            Text("$duplicate entradas repetidas por SHA · ${summary.optLong("unsupported_or_unrecognized")} formatos em revisão")
                            Numeric("Partes esperadas (opcional)", model.expectedArchives, model::setArchiveExpectation, Modifier.fillMaxWidth(), !model.busy)
                            model.expectedArchives.toIntOrNull()?.let { expected ->
                                val missing = (expected - summary.optInt("archives_verified")).coerceAtLeast(0)
                                if (missing > 0) Text("$missing partes esperadas ainda sem verificação.")
                            }
                            Text("Selecione partes adicionais de outros exports quando necessário. A cobertura total do Google Fotos exige conferência do manifesto do Takeout.")
                        }
                        items(model.sources, key = { it.id }) { Text("${it.name ?: "Parte ${it.id}"} · ${it.state}") }
                        item {
                            OutlinedButton(onClick = actions.pickLocal, enabled = !model.busy && model.sources.isNotEmpty()) {
                                Text("Comparar arquivos locais por SHA-256")
                            }
                        }
                    }
                    Screen.BATCH -> {
                      item {
                        Text("Preparar cópias locais", style = MaterialTheme.typography.titleLarge)
                        Text("Use Wi-Fi não tarifada. Um lote de 20 GiB pode transferir cerca de 20 GiB de mídia ou mais com novas tentativas. A Samsung Gallery realiza a sincronização.")
                        Text("Excluir uma mídia sincronizada na Gallery pode removê-la do armazenamento Google e dos Galaxy conectados. Confira a conta, o álbum e a privacidade antes de publicar.")
                        Toggle("Li o aviso e quero preparar cópias visíveis no dispositivo", model.consent, model::acknowledge, !model.busy)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Numeric("Reserva em GiB", model.reserve, { model.reserve = it }, Modifier.weight(1f), !model.busy)
                            Numeric("Limite em GiB", model.maxGiB, { model.maxGiB = it }, Modifier.weight(1f), !model.busy)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Numeric("Até N arquivos", model.limit, { model.limit = it }, Modifier.weight(1f), !model.busy)
                            Numeric("Bateria mínima (%)", model.minimumBattery, { model.minimumBattery = it }, Modifier.weight(1f), !model.busy)
                        }
                        Toggle("Processar somente com carregador", model.onlyCharging, { model.onlyCharging = it }, !model.busy)
                        if (model.active == null) {
                            if (!model.pilot) Text("O piloto inclui até 40 mídias elegíveis. Revise os itens antes de publicar.")
                            Button(onClick = model::previewBatch, enabled = !model.busy && model.consent) { Text("Prévia do lote") }
                        } else Button(onClick = model::resume, enabled = !model.busy && model.consent) {
                            Text("Retomar lote ${model.active}")
                        }
                        HorizontalDivider()
                        Text("Sincronização pela Samsung", style = MaterialTheme.typography.titleLarge)
                        Text("Na Gallery, abra Configurações → Sincronizar fotos e vídeos. Confira a conta Google e habilite o álbum GalleryBridge correspondente, se o aparelho oferecer essa opção.")
                        Text("Abra uma amostra de cada formato do piloto e confira data, exibição e se o item está acessível na conta correta. Revisão visual registra sua observação. Vínculo técnico e verificação remota permanecem desconhecidos.")
                        OutlinedButton(onClick = actions.openGallery, enabled = !model.busy && model.galleryAvailable) { Text("Abrir Samsung Gallery") }
                        if (!model.galleryAvailable) Text("UNSUPPORTED · Samsung Gallery indisponível. O fluxo local continua disponível.")
                        if (model.last != null && model.active == null && !model.pilot) {
                            Toggle("Conferi visualmente a amostra do piloto", reviewed, { reviewed = it }, !model.busy)
                            OutlinedButton(onClick = model::attest, enabled = reviewed && !model.busy) { Text("Registrar revisão do piloto") }
                        }
                        Text("Liberação de espaço bloqueada no assistente enquanto a evidência por item estiver ausente. O recurso nativo da Samsung é distinto de excluir mídia. Após uma liberação feita na Gallery, use a conferência local para registrar quais cópias deixaram de estar acessíveis.")
                        OutlinedButton(onClick = model::audit, enabled = !model.busy && model.last != null) { Text("Conferir MediaStore") }
                    }
                        items(model.batches, key = { it.id }) { batch ->
                            Text("Lote ${batch.id} · ${batch.prepared}/${batch.planned} preparados · ${formatGiB(batch.bytes)} observados localmente")
                            if (batch.absent > 0) Text("${batch.absent} cópias ausentes em revisão")
                            Text(if (batch.state == "STAGING") "Preparação incompleta" else "Associação remota por item sem comprovação")
                        }
                    }
                    Screen.METADATA -> {
                        item {
                            Text("Revisão de metadados", style = MaterialTheme.typography.titleLarge)
                            Button(onClick = model::previewFavorites, enabled = !model.busy && !model.pendingConsent) {
                                Text("Conferir favoritos históricos")
                            }
                            if (model.pendingConsent) {
                                OutlinedButton(onClick = model::recoverFavorites, enabled = !model.busy) {
                                    Text("Conferir pedido de favoritos interrompido")
                                }
                            }
                            Text("Abra um item para comparar proveniência e registrar a decisão. Vídeos aguardam aprovação individual para cópia independente. A associação Apple Live Photo depende do identificador interno.")
                            Row {
                                TextButton(onClick = { model.refresh(maxOf(0, model.pageOffset - 25)) },
                                    enabled = model.pageOffset > 0 && !model.busy) { Text("Anterior") }
                                TextButton(onClick = { model.refresh(model.pageOffset + 25) },
                                    enabled = model.page.size == 25 && !model.busy) { Text("Próxima") }
                            }
                        }
                        items(model.page, key = { it.sha }) { item ->
                            OutlinedButton(onClick = { model.inspect(item) }, enabled = !model.busy) {
                                Text(item.path.substringAfterLast('/'), maxLines = 2)
                            }
                        }
                    }
                    Screen.REPORT -> item {
                        Text("Evidência local e pendências", style = MaterialTheme.typography.titleLarge)
                        Text("${summary.optLong("media_store_receipts")} recibos locais · ${summary.optLong("local_absence_unknown")} ausências em revisão")
                        Text("${summary.optLong("local_exact_matches")} correspondências locais por SHA · ${summary.optLong("field_decisions")} decisões registradas")
                        Text("Verificação remota independente indisponível.")
                        Text("${summary.optLong("favorite_local_verified")} favoritos aplicados e conferidos localmente")
                        Text("${summary.optLong("favorite_sdk_clean_positive")} favoritos observados no MediaSync · ${summary.optLong("favorite_remote_visual_positive")} observações manuais positivas no Google Fotos")
                        Text("${summary.optLong("prepared_metadata_edits")} ajustes autorizados · ${summary.optLong("verified_derivative_copies")} cópias derivadas verificadas.")
                        Text("${summary.optLong("indexed_dates_verified")} datas conferidas no índice · ${summary.optLong("indexed_dates_review")} sem índice correspondente.")
                        Text("Histórico importado · ${summary.optLong("historical_cloud_pending")} pendências remotas sem resolução.")
                        TextButton(onClick = { historical = true }, enabled = !model.busy) { Text("Registrar dez pendências do estudo") }
                        Button(onClick = actions.export, enabled = !model.busy) { Text("Exportar relatório sanitizado") }
                        OutlinedButton(onClick = { exportCatalog = true }, enabled = !model.busy) { Text("Exportar catálogo por hash") }
                    }
                    Screen.DIAGNOSTICS -> item {
                        Text("Diagnóstico avançado", style = MaterialTheme.typography.titleLarge)
                        Toggle("Exibir e incluir métricas do assistente", model.diagnostics, { model.diagnostics = it }, !model.busy)
                        if (model.diagnostics) {
                            model.snapshot?.let {
                                Text("Wi-Fi não tarifada: ${if (it.wifi) "sim" else "não"} · bateria ${it.battery ?: "?"}% · térmico ${it.thermal} · margem ${it.headroom ?: "indisponível"}")
                                Text("Espaço livre ${formatGiB(it.free)} · UID do assistente RX ${it.helperRx ?: "indisponível"}, TX ${it.helperTx ?: "indisponível"} bytes.")
                            }
                            Text("Estado nativo e causa de TRANSIENT_ERROR · UNKNOWN. Contadores do provedor Drive e da Samsung não são acessíveis neste modo.")
                            OutlinedButton(onClick = { rootConsent = true }, enabled = !model.busy) {
                                Text("Ler contadores avançados do MediaSync")
                            }
                            model.sdkDiagnostics?.let {
                                Text(it.status)
                                if (it.associated != null) Text("Associações sinalizadas: ${it.associated} · fila: ${it.queue} · tentativas: ${it.retried} · registros de backup: ${it.backup}.")
                            }
                            model.providerVersions.forEach { Text("${it.first} · ${it.second ?: "indisponível"}") }
                            TextButton(onClick = { model.refresh() }, enabled = !model.busy) { Text("Atualizar indicadores") }
                        }
                    }
                }
            }
        }
    }

    model.plan?.let { plan ->
        AlertDialog(onDismissRequest = { model.plan = null }, title = { Text("Prévia antes da publicação") },
            text = {
                LazyColumn(Modifier.heightIn(max = 380.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item {
                        Text("${plan.size} arquivos · ${formatGiB(plan.sumOf { it.size })} de mídia. A Samsung pode transferir todos esses bytes e repetir tentativas.")
                        Text("Confira se algum item deve permanecer privado. Exclusões ficam disponíveis em Metadados.")
                    }
                    items(plan, key = { it.sha }) { Text(it.path.substringAfterLast('/')) }
                }
            },
            confirmButton = { Button(enabled = !model.busy, onClick = { model.plan = null; model.applyBatch(plan) }) { Text("Publicar cópias locais") } },
            dismissButton = { TextButton(onClick = { model.plan = null }) { Text("Voltar") } })
    }
    model.detail?.let { MetadataDialog(model, actions, it) }
    model.favoritePlan?.let { FavoriteDialog(model, actions, it) }
    if (rootConsent) AlertDialog(onDismissRequest = { rootConsent = false },
        title = { Text("Diagnóstico avançado somente leitura") },
        text = { Text("Solicita root para consultar contagens no banco do agente Samsung, se a versão tiver perfil conhecido. O resultado exclui identificadores e só representa o banco observado.") },
        confirmButton = { Button(onClick = { rootConsent = false; model.readSdkDiagnostics() }) { Text("Consultar contadores") } },
        dismissButton = { TextButton(onClick = { rootConsent = false }) { Text("Cancelar") } })
    if (exportCatalog) {
        AlertDialog(onDismissRequest = { exportCatalog = false }, title = { Text("Exportar identificadores de mídia") },
            text = { Text("O catálogo inclui SHA-256 e proveniência de arquivos. Guarde o JSONL em local privado. A linha end indica que a exportação foi concluída.") },
            confirmButton = { Button(onClick = { exportCatalog = false; actions.exportCatalog() }) { Text("Escolher destino") } },
            dismissButton = { TextButton(onClick = { exportCatalog = false }) { Text("Voltar") } })
    }
    if (historical) {
        AlertDialog(onDismissRequest = { historical = false }, title = { Text("Pendências do estudo anterior") },
            text = { Text("Registra nove pendências do lote 002 e uma do lote 003 como histórico separado das importações atuais.") },
            confirmButton = { Button(onClick = { historical = false; model.history() }) { Text("Registrar") } },
            dismissButton = { TextButton(onClick = { historical = false }) { Text("Voltar") } })
    }
}

private fun formatGiB(bytes: Long) = String.format(Locale.forLanguageTag("pt-BR"), "%.2f GiB", bytes.toDouble() / GIB)

@Composable
private fun Toggle(label: String, checked: Boolean, action: (Boolean) -> Unit, enabled: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, action, enabled = enabled)
        Text(label)
    }
}

@Composable
private fun Numeric(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier, enabled: Boolean) {
    OutlinedTextField(value, onChange, modifier, enabled = enabled, label = { Text(label) },
        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}

internal data class Preview(val item: Item, val takeout: Sidecar?, val albums: List<String>, val approvedVideo: Boolean,
    val confirmedPair: Boolean, val excluded: Boolean, val restricted: Boolean,
    val edits: List<PreparationEdit>, val canExportMotion: Boolean,
    val favoriteSources: List<FavoriteSource> = emptyList(),
    val favoriteEvidence: Map<FavoriteObservationLevel, String> = emptyMap()) {
    val photosKey: String? get() {
        if (historicalFavorite(favoriteSources.flatMap { it.flags }) != HistoricalFavorite.FAVORITED) return null
        return favoriteSources.mapNotNull { it.photosKey }.distinct().singleOrNull()
    }
    private val media = item.metadata?.let(::JSONObject)
    val exif: JSONObject? get() = media?.optJSONObject("exif")
    val keys: JSONObject? get() = media?.optJSONObject("keys")
    val container: JSONObject? get() = media?.optJSONObject("container")
    val description: String? get() = media?.optString("description")?.takeIf(String::isNotBlank)
    val orientation: String? get() = media?.optString("orientation")?.takeIf(String::isNotBlank)
    val gps: Pair<Double, Double>? get() = media?.takeIf { it.has("latitude") && it.has("longitude") }
        ?.let { it.optDouble("latitude") to it.optDouble("longitude") }
    val internalId: String? get() = media?.optString("live_id")?.let(::normalizeLiveId)
    val observed: Instant? get() = item.observed?.let(Instant::ofEpochMilli)

    fun group(field: Field): String? {
        if (field != Field.CAPTURE_TIME || item.state != "NEW") return null
        val tag = keys ?: exif ?: return null
        val instant = tag.epoch()?.let(Instant::ofEpochMilli) ?: return null
        val source = tag.optString("source")
        val counterpart = if (keys != null) container?.epoch()?.let(Instant::ofEpochMilli) else takeout?.taken?.instant
        val conflict = compareTime(TimeFact("", source, instant, null), counterpart?.let { TimeFact("", "", it, null) })
        if (conflict in listOf(TimeConflict.NO_CONFLICT, TimeConflict.CONTEXT_REQUIRED)) return null
        return "$field/$conflict/$source/${item.mime}/${item.source.id}/${instant.toString().take(4)}"
    }

    fun values(field: Field): String {
        fun JSONObject?.time() = this?.let { "${it.optString("raw")} (${it.optString("basis")})" } ?: "ausente"
        return when (field) {
            Field.CAPTURE_TIME -> "Takeout: ${takeout?.taken?.instant ?: "ausente"}\nEXIF: ${exif.time()}\nKeys: ${keys.time()}\nQuickTime: ${container.time()}\nMediaStore: ${observed ?: "desconhecido"}"
            Field.LOCATION -> "Takeout: ${takeout?.locations?.joinToString { "${it.source} ${it.lat}, ${it.lng}" }.orEmpty().ifEmpty { "ausente" }}\nEXIF: ${gps ?: "ausente"}"
            Field.FAVORITE -> "Takeout: ${takeout?.favorite ?: "desconhecido"}\nA operação pública depende de um URI local verificado e consentimento do Android."
            Field.DESCRIPTION -> "Takeout: ${takeout?.description?.take(500) ?: "ausente"}\nEXIF: ${description?.take(500) ?: "ausente"}"
            Field.ALBUM -> "Pastas no Takeout:\n${albums.take(8).joinToString("\n")}\nUma mídia pode conservar várias associações no catálogo."
            Field.ORIENTATION -> "Orientação EXIF: ${orientation ?: "ausente"}"
            Field.EDIT_VARIANT -> "Edição e variantes exigem revisão manual."
        }
    }
}

@Composable
private fun FavoriteEvidencePanel(
    preview: Preview, inspectLocal: (Preview) -> Unit, openRemote: (String) -> Unit, busy: Boolean
) {
    val evidence = preview.favoriteEvidence
    Text("MediaStore · " + favoriteObservation(evidence[FavoriteObservationLevel.LOCAL_STATE]))
    Text("MediaSync · " + favoriteObservation(evidence[FavoriteObservationLevel.SDK_CACHE]))
    Text("Google Fotos, revisão manual · " + favoriteObservation(evidence[FavoriteObservationLevel.REMOTE_VISUAL]))
    Text("Confirmação remota independente pendente.")
    if (preview.favoriteSources.isNotEmpty()) {
        val origins = preview.favoriteSources.take(5).joinToString("\n") { row ->
            val flags = row.flags.sorted().joinToString(" / ") { if (it) "favorito" else "sem favorito" }
            "Parte ${row.part}, entrada ${row.entry + 1} · $flags"
        }
        Text(origins)
        if (preview.favoriteSources.size > 5) Text("+${preview.favoriteSources.size - 5} observações na origem")
    }
    TextButton(onClick = { inspectLocal(preview) }, enabled = !busy) { Text("Reler favorito local") }
    preview.photosKey?.let { key ->
        TextButton(onClick = { openRemote(key) }, enabled = !busy) { Text("Conferir item original no Google Fotos") }
    }
}

private fun favoriteObservation(value: String?): String = when (value) {
    "FAVORITED", "CLEAN_FAVORITE", "LOCAL_VERIFIED", "ALREADY_LOCAL" -> "favorito observado"
    "NOT_FAVORITED", "CLEAN_NOT_FAVORITE", "READY" -> "sem favorito observado"
    "NO_RECORD" -> "sem associação"
    "AMBIGUOUS" -> "associação ambígua"
    "DIRTY" -> "commit pendente"
    "CLEAN_UNKNOWN" -> "associação limpa, favorito desconhecido"
    else -> "não verificado"
}

@Composable
private fun MetadataDialog(model: BridgeModel, actions: Actions, preview: Preview) {
    var field by remember(preview.item.sha) { mutableStateOf(model.detailField) }
    var action by remember(preview.item.sha, field) { mutableStateOf<Action?>(null) }
    var similar by remember(preview.item.sha, field) { mutableStateOf(emptyList<Preview>()) }
    var manual by remember(preview.item.sha, field) { mutableStateOf("") }
    var exceptions by remember(preview.item.sha, field) { mutableStateOf(emptySet<String>()) }
    var confirmingSdk by remember(preview.item.sha) { mutableStateOf(false) }
    var confirmingMotion by remember(preview.item.sha) { mutableStateOf(false) }
    var confirmingManual by remember(preview.item.sha, field) { mutableStateOf(false) }
    var approvingVideo by remember(preview.item.sha) { mutableStateOf(false) }
    var favorite by remember(preview.item.sha) { mutableStateOf<Boolean?>(null) }
    var confirmingVisual by remember(preview.item.sha) { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val selected = action
    val favoriteSelection = favorite
    val fields = listOf("Data e hora", "Localização", "Favorito", "Descrição", "Álbum", "Orientação", "Edição")
    AlertDialog(onDismissRequest = { model.detail = null },
        title = { Text(preview.item.path.substringAfterLast('/'), maxLines = 2) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Origem ${preview.item.source.id} · entrada ${preview.item.ordinal} · ${preview.item.mime}")
                if (preview.confirmedPair) Text("Par identificado pelos identificadores internos. Reprodução Samsung em revisão.")
                else if (preview.internalId != null) Text("Identificador interno presente, sem par inequívoco.")
                if (model.diagnostics && !preview.restricted && !preview.excluded && field == Field.FAVORITE) {
                    if (confirmingSdk) {
                        Text("A leitura por item pede root se o agente corresponder ao perfil.")
                        TextButton(onClick = { confirmingSdk = false; model.inspectSdk(preview.item) },
                            enabled = !model.busy) { Text("Consultar associação deste item") }
                    } else TextButton(onClick = { confirmingSdk = true }) { Text("Revisar observação pelo SDK") }
                }
                if (field == Field.FAVORITE && !preview.restricted && !preview.excluded) {
                    FavoriteEvidencePanel(preview,
                        inspectLocal = model::observeCurrentFavorite,
                        openRemote = { key ->
                            val link = android.net.Uri.parse("https://photos.google.com/photo/$key")
                            try {
                                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, link))
                                confirmingVisual = true
                            } catch (_: android.content.ActivityNotFoundException) {
                                model.message = "Nenhum app disponível para abrir o item original."
                            }
                        },
                        busy = model.busy)
                    if (confirmingVisual) {
                        Text("Confira o ícone de favorito no item original da conta que gerou o Takeout.")
                        TextButton(onClick = { confirmingVisual = false; model.recordRemoteFavorite(preview, true) },
                            enabled = !model.busy) { Text("Vi o item favorito") }
                        TextButton(onClick = { confirmingVisual = false; model.recordRemoteFavorite(preview, false) },
                            enabled = !model.busy) { Text("Vi o item sem favorito") }
                        TextButton(onClick = { confirmingVisual = false }) { Text("Não consegui conferir") }
                    }
                }
                if (preview.canExportMotion) {
                    if (confirmingMotion) {
                        Text("Exporta cópia JPEG + vídeo para uma pasta escolhida. Guarde fora de álbuns sincronizados até testar a reprodução.")
                        Button(onClick = { confirmingMotion = false; actions.motion() }, enabled = !model.busy) {
                            Text("Escolher destino do teste")
                        }
                    } else TextButton(onClick = { confirmingMotion = true }) { Text("Revisar exportação Motion Photo") }
                }
                if (preview.restricted) Text("Privado ou protegido por .nomedia. Publicação bloqueada.")
                if (!preview.restricted && preview.item.state == "NEW") {
                    Toggle("Excluir dos próximos lotes", preview.excluded,
                        { model.exclude(preview, it) }, !model.busy)
                }
                if (preview.item.mime.startsWith("video/") && !preview.approvedVideo && !preview.excluded && !preview.restricted) {
                    if (approvingVideo) {
                        Text("A cópia independente pode criar um vídeo avulso de uma Live Photo. Confirme só se essa for a intenção.")
                        OutlinedButton(onClick = { approvingVideo = false; model.approveVideo(preview) },
                            enabled = !model.busy) { Text("Confirmar cópia independente") }
                    } else TextButton(onClick = { approvingVideo = true }) { Text("Revisar elegibilidade do vídeo") }
                }
                Field.entries.forEachIndexed { index, entry ->
                    TextButton(onClick = { field = entry }, enabled = entry != field && !model.busy) { Text(fields[index]) }
                }
                Text(fields[field.ordinal], style = MaterialTheme.typography.titleMedium)
                Text(preview.values(field))
                preview.edits.singleOrNull { it.field == field }?.let {
                    Text("Ajuste autorizado para a próxima preparação: ${it.value}")
                }
                if (preview.item.state == "NEW") {
                    Text("Ajustes compatíveis são gravados e conferidos na próxima cópia. O original fica no ZIP.")
                } else Text("Item já publicado ou em preparação. A alteração desta cópia exige revisão específica.")
                if (field in setOf(Field.CAPTURE_TIME, Field.LOCATION, Field.DESCRIPTION, Field.ALBUM, Field.ORIENTATION) &&
                    !preview.excluded && !preview.restricted && preview.item.state == "NEW" && selected == null &&
                    favoriteSelection == null) {
                    val hint = when (field) {
                        Field.CAPTURE_TIME -> "ISO com offset, ex. 2024-01-31T10:30:00-03:00"
                        Field.LOCATION -> "Latitude, longitude"
                        Field.ORIENTATION -> "Orientação EXIF (1 a 8)"
                        else -> "Valor para a cópia"
                    }
                    OutlinedTextField(manual, { manual = it; confirmingManual = false }, label = { Text(hint) })
                    val draft = validatedPreparation(preview.item, field, manual)
                    if (draft != null) {
                        if (confirmingManual) {
                            Text("Prévia da alteração: ${draft.value}")
                            Button(onClick = {
                                model.applyDecision(preview, field, Action.REVIEW, emptyList(), draft.value)
                                confirmingManual = false
                            }, enabled = !model.busy) { Text("Autorizar nesta cópia") }
                        } else TextButton(onClick = { confirmingManual = true }) { Text("Revisar valor manual") }
                    }
                }
                if (selected == null && favoriteSelection == null) {
                    listOf(
                        Action.KEEP_DESTINATION to "Preservar destino", Action.USE_TAKEOUT to "Usar valor do Takeout",
                        Action.USE_FILE_METADATA to "Usar metadado do arquivo",
                        Action.REVIEW to "Revisar individualmente", Action.REPORT_ONLY to "Somente relatório"
                    ).forEach { (candidate, label) ->
                        OutlinedButton(onClick = { action = candidate }, enabled = !model.busy) { Text(label) }
                    }
                    if (field == Field.FAVORITE && !model.pendingConsent && Build.VERSION.SDK_INT >= 30) {
                        OutlinedButton(onClick = { favorite = true }) { Text("Preparar favorito local") }
                        OutlinedButton(onClick = { favorite = false }) { Text("Preparar remoção do favorito local") }
                    }
                } else if (selected != null) {
                    val chosen = similar.filter { it.item.sha !in exceptions }
                    val draft = suggestedPreparation(preview.item, preview.takeout, preview.albums, field, selected)
                    Text("Prévia · $selected · ${if (similar.isEmpty()) 1 else chosen.size} item(ns).")
                    if (draft != null) Text("Valor candidato: ${draft.value}")
                    else Text("Sem valor inequívoco para escrita neste estágio. A decisão fica em revisão.")
                    if (preview.group(field) != null && similar.isEmpty()) {
                        TextButton(onClick = { model.findSimilar(preview, field) { similar = it } },
                            enabled = !model.busy) { Text("Ver conflitos equivalentes desta origem") }
                    }
                    if (similar.isNotEmpty()) {
                        similar.take(200).forEach { candidate ->
                            Toggle(candidate.item.path.substringAfterLast('/'), candidate.item.sha !in exceptions, { checked ->
                                exceptions = if (checked) exceptions - candidate.item.sha else exceptions + candidate.item.sha
                            }, !model.busy)
                        }
                        if (similar.size > 200) Text("Grupo acima de 200 itens. Revise estes conflitos individualmente.")
                    }
                    Button(onClick = {
                        model.applyDecision(preview, field, selected, chosen, null)
                        action = null
                        similar = emptyList()
                    }, enabled = !model.busy && similar.size <= 200 && (similar.isEmpty() || chosen.isNotEmpty())) {
                        Text(if (draft == null) "Registrar decisão" else "Autorizar preparação")
                    }
                    TextButton(onClick = { action = null; similar = emptyList(); exceptions = emptySet() }) { Text("Cancelar prévia") }
                } else if (favoriteSelection != null) {
                    Text("Prévia · ${if (favoriteSelection) "marcar" else "desmarcar"} favorito neste item local. O Android pedirá confirmação.")
                    Button(onClick = { favorite = null; actions.favorite(preview.item, favoriteSelection) },
                        enabled = !model.busy) { Text("Continuar ao consentimento") }
                    TextButton(onClick = { favorite = null }) { Text("Cancelar prévia") }
                }
            }
        },
        confirmButton = { TextButton(onClick = { model.detail = null }) { Text("Fechar") } })
}

internal fun FavoriteState.label(): String = when (this) {
    FavoriteState.SOURCE_CONFLICT -> "Takeout contém flags contraditórias"
    FavoriteState.NOT_STAGED -> "aguarda mídia local com identidade comprovada"
    FavoriteState.NEEDS_NATIVE_RESTORE -> "baixe o item existente pela Samsung Gallery e confira novamente"
    FavoriteState.AMBIGUOUS_DESTINATION -> "há mais de uma cópia local"
    FavoriteState.ACCESS_REQUIRED -> "selecione novamente a mídia local para dar acesso"
    FavoriteState.READY -> "pronto para solicitar consentimento"
    FavoriteState.ALREADY_LOCAL -> "valor já observado no MediaStore"
    FavoriteState.AWAITING_CONSENT -> "consentimento interrompido"
    FavoriteState.LOCAL_VERIFIED -> "alteração local verificada"
    FavoriteState.NOT_APPLIED -> "valor local ainda divergente"
    FavoriteState.PROVIDER_REJECTED -> "o provedor recusou a operação"
}

@Composable
private fun FavoriteDialog(model: BridgeModel, actions: Actions, entries: List<FavoriteReview>) {
    var exceptions by remember(entries) { mutableStateOf(emptySet<String>()) }
    val eligible = entries.mapNotNull { it.change }.filter { it.sha !in exceptions }.take(100)
    AlertDialog(onDismissRequest = { model.favoritePlan = null },
        title = { Text("Favoritos históricos") },
        text = {
            LazyColumn(Modifier.heightIn(max = 390.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    Text("${eligible.size} itens deste pedido · ${entries.size} candidatos históricos")
                    Text("O Android pedirá consentimento. O assistente verifica a marcação no MediaStore. O envio ao Google Fotos permanece pendente.")
                }
                items(entries, key = { it.item.sha }) { entry ->
                    val name = entry.item.path.substringAfterLast('/')
                    if (entry.change == null) {
                        TextButton(onClick = {
                            model.favoritePlan = null
                            model.inspect(entry.item, Field.FAVORITE)
                        }, enabled = !model.busy) { Text("$name · ${entry.state.label()}") }
                        if (entry.state == FavoriteState.NEEDS_NATIVE_RESTORE) {
                            Text("Na Samsung Gallery, abra o item existente e use a opção de baixar o original, se disponível. Depois confira o MediaStore na tela Lotes e selecione o arquivo baixado em Origens.")
                            TextButton(onClick = actions.openGallery, enabled = !model.busy && model.galleryAvailable) {
                                Text("Abrir Samsung Gallery")
                            }
                        }
                    } else Toggle(name, entry.item.sha !in exceptions, { checked ->
                        exceptions = if (checked) exceptions - entry.item.sha else exceptions + entry.item.sha
                    }, !model.busy)
                }
            }
        },
        confirmButton = {
            Button(onClick = { model.favoritePlan = null; actions.favorites(eligible) },
                enabled = eligible.isNotEmpty() && !model.busy) { Text("Solicitar marcação") }
        },
        dismissButton = { TextButton(onClick = { model.favoritePlan = null }) { Text("Fechar") } })
}
