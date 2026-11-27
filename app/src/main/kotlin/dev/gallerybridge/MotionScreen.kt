package dev.gallerybridge

import android.app.PendingIntent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

internal data class MotionInventoryEntry(val photo: Item, val video: Item?, val status: String)
internal data class MotionActions(
    val selectSurvivor: () -> Unit, val selectCandidate: () -> Unit,
    val consent: (PendingIntent) -> Unit, val open: (Uri) -> Unit
)

internal fun LazyListScope.motionScreen(model: BridgeModel, actions: Actions) {
    item {
        Text("Live Photos e fotos com movimento", style = MaterialTheme.typography.titleLarge)
        Text("Analise os ZIPs na aba Origem. Os pares são identificados pelo código interno da foto e do vídeo.")
        Button(onClick = { model.inventoryMotion() }, enabled = !model.busy) { Text("Analisar pares") }
        val ready = model.motionInventory.filter { it.status == "Par confirmado" }
        if (ready.isNotEmpty()) {
            val mb = ready.sumOf { it.photo.size + (it.video?.size ?: 0) } / 1_000_000
            Text("${ready.size} pares para preparar nesta página · cerca de $mb MB de saída.")
        }
        Text("A recomposição fica no app até você publicar. A Galeria pode enviar todos os bytes da cópia.")
        OutlinedButton(onClick = model::composeMotionPage, enabled = !model.busy && ready.isNotEmpty()) {
            Text("Recompor pares desta página")
        }
        Row {
            TextButton(onClick = { model.inventoryMotion(maxOf(0, model.motionOffset - 24)) },
                enabled = !model.busy && model.motionOffset > 0) { Text("Anterior") }
            TextButton(onClick = { model.inventoryMotion(model.motionOffset + 24) },
                enabled = !model.busy && model.motionInventory.size == 24) { Text("Próxima") }
        }
    }
    items(model.motionInventory, key = { "motion-${it.photo.sha}" }) { entry ->
        var preview by remember(entry.photo.sha) { mutableStateOf(false) }
        Text(entry.photo.path.substringAfterLast('/'), style = MaterialTheme.typography.titleMedium)
        entry.video?.let { Text(it.path.substringAfterLast('/')) }
        Text(entry.status)
        if (entry.status == "Recomposta e pronta para publicar") {
            OutlinedButton(onClick = { preview = true }, enabled = !model.busy) { Text("Revisar publicação") }
        }
        if (preview) AlertDialog(
            onDismissRequest = { preview = false },
            title = { Text("Publicar uma cópia local") },
            text = { Text("Destino Pictures/GalleryBridge_Motion. Confirme a reprodução da cópia na Galeria Samsung antes de sincronizar. O original permanece no ZIP.") },
            confirmButton = {
                Button(onClick = { preview = false; model.publishMotion(entry.photo) }) { Text("Publicar cópia") }
            },
            dismissButton = { TextButton(onClick = { preview = false }) { Text("Voltar") } }
        )
    }
    item {
        model.lastMotionUri?.let { uri ->
            OutlinedButton(onClick = { actions.motionActions.open(uri) }) { Text("Abrir cópia mais recente") }
        }
        Text("Revisar possíveis redundâncias", style = MaterialTheme.typography.titleLarge)
        Text("Escolha a foto com movimento que deve permanecer. Para comparar um item histórico da nuvem, baixe-o pela Galeria e selecione a cópia local. Operações diretas em itens disponíveis só na nuvem são indisponíveis.")
        OutlinedButton(onClick = actions.motionActions.selectSurvivor, enabled = !model.busy) {
            Text("Escolher foto que deve permanecer")
        }
        OutlinedButton(onClick = actions.motionActions.selectCandidate,
            enabled = !model.busy && model.motionSurvivor != null) { Text("Comparar possível redundante") }
        model.motionOperation?.let { op ->
            val label = when (op.state) {
                "MOVED_LOCAL" -> "Movida para revisão no aparelho"
                "REMOVAL_OBSERVED" -> "Confirmada na lixeira do aparelho"
                "RESTORED_LOCAL" -> "Restaurada no aparelho"
                "RESTORED_PATH" -> "Devolvida à pasta de origem"
                "CANCELLED" -> "Solicitação cancelada"
                else -> "Em revisão"
            }
            Text("$label. Efeito remoto desconhecido.")
            if (op.state.endsWith("_REQUESTED")) {
                TextButton(onClick = { model.finishMotionOperation(null) }, enabled = !model.busy) { Text("Reconferir operação") }
            }
            if (op.state == "MOVED_LOCAL" && op.action == "MOVE") {
                TextButton(onClick = model::undoMotionMove, enabled = !model.busy) { Text("Desfazer movimento local") }
            }
            if (op.state == "REMOVAL_OBSERVED") {
                TextButton(onClick = { model.restoreMotion(actions.motionActions.consent) },
                    enabled = !model.busy) { Text("Restaurar da lixeira") }
            }
        }
        OutlinedButton(onClick = actions.openGallery, enabled = model.galleryAvailable && !model.busy) {
            Text("Abrir Galeria Samsung para conferir sincronização")
        }
        OutlinedButton(onClick = actions.export, enabled = !model.busy) { Text("Exportar relatório") }
    }
}

@Composable
internal fun MotionReviewDialog(model: BridgeModel, plan: MotionComparison, actions: MotionActions) {
    var playback by remember(plan.key) { mutableStateOf(false) }
    var fields by remember(plan.key) { mutableStateOf(false) }
    var localOnly by remember(plan.key) { mutableStateOf(false) }
    var confirmDiagnostic by remember(plan.key) { mutableStateOf(false) }
    val context = LocalContext.current
    val attestation = MotionAttestation(playback, fields, localOnly)
    val canReview = plan.eligible && playback && fields && !model.busy
    AlertDialog(
        onDismissRequest = model::comparisonDismiss,
        title = { Text("Revisar a foto que permanece") },
        text = {
            Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Permanece", style = MaterialTheme.typography.labelLarge)
                        Text(plan.survivor.name)
                        Text(plan.survivor.path)
                        TextButton(onClick = { actions.open(plan.survivor.uri) }) { Text("Abrir") }
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Em revisão", style = MaterialTheme.typography.labelLarge)
                        Text(plan.candidate.name)
                        Text(plan.candidate.path)
                        TextButton(onClick = { actions.open(plan.candidate.uri) }) { Text("Abrir") }
                    }
                }
                Text(plan.reason)
                if (model.diagnostics) {
                    if (confirmDiagnostic) {
                        TextButton(enabled = !model.busy, onClick = { confirmDiagnostic = false; model.observeMotionLink(plan) }) {
                            Text("Confirmar leitura do MediaSync com root")
                        }
                    } else TextButton(onClick = { confirmDiagnostic = true }) { Text("Consultar vínculo local no MediaSync") }
                }
                if (plan.eligible) {
                    Text("O vídeo incorporado e o still foram conferidos pelos bytes do ZIP. A Galeria é responsável por reprodução e sincronização.")
                    MotionCheck("Reproduzi o movimento da foto que permanece e conferi o áudio quando presente.", playback) { playback = it }
                    MotionCheck("Conferi data, favorito, localização, legenda, qualidade, álbuns e compartilhamentos.", fields) { fields = it }
                    MotionCheck("Conferi na Galeria que a mídia a retirar está somente neste aparelho. Se houver dúvida, vou preservar.", localOnly) { localOnly = it }
                    Text("Uma exclusão sincronizada pode afetar o Google Fotos e outros aparelhos. Os originais do Takeout serão preservados.")
                    if (Build.VERSION.SDK_INT >= 30) {
                        Button(enabled = canReview && localOnly,
                            onClick = { model.trashMotion(plan, attestation, actions.consent) }) { Text("Enviar redundante à lixeira") }
                    }
                    if (plan.candidate.owner == context.packageName && !plan.candidate.path.contains("/GalleryBridge_Revisao/")) {
                        OutlinedButton(enabled = canReview && localOnly, onClick = { model.moveMotion(plan, attestation) }) {
                            Text("Mover para o álbum GalleryBridge_Revisao")
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = model::comparisonDismiss) { Text("Preservar ambas") } }
    )
}

@Composable
private fun MotionCheck(label: String, value: Boolean, changed: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value, onCheckedChange = changed)
        Text(label)
    }
}
