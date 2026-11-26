package dev.gallerybridge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DuplicateScreen(model: BridgeModel, actions: Actions) {
    Text("Confira arquivos selecionados por conteúdo. Favoritos e JPEGs com movimento ou estruturas desconhecidas ficam protegidos.")
    OutlinedButton(onClick = actions.selectDuplicates, enabled = !model.busy) { Text("Selecionar fotos") }
    OutlinedButton(onClick = { model.finishDuplicateMoves(null) }, enabled = !model.busy) {
        Text("Conferir movimentos interrompidos")
    }
    model.duplicateSummary.forEach { (state, quantity) -> Text("$state · $quantity") }
    val plan = model.duplicatePreview ?: return
    var dialog by remember(plan.digest) { mutableStateOf(false) }
    var checked by remember(plan.digest) { mutableStateOf(false) }
    Text("${plan.candidates.size} fotos revisáveis neste lote. ${plan.held} arquivos não puderam ser avaliados.")
    OutlinedButton(onClick = { dialog = true }, enabled = plan.candidates.isNotEmpty() && !model.busy) {
        Text("Revisar seleção do lote")
    }
    if (dialog) AlertDialog(
        onDismissRequest = { dialog = false },
        title = { Text("Revisar ${plan.candidates.size} cópias") },
        text = {
            Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                for (group in plan.groups) {
                    Text("Permanece: ${group.keeper.path}${group.keeper.name}")
                    group.candidates.forEach { Text("Vai para revisão: ${it.path}${it.name}") }
                }
                Text("Destino: $DUPLICATE_DESTINATION. Confirme álbuns, compartilhamentos e que os itens a mover estão somente no aparelho.")
                Checkbox(checked, { checked = it })
                Text("Conferi todos os itens listados e quero mover este lote.")
            }
        },
        confirmButton = {
            Button(enabled = checked && !model.busy, onClick = {
                dialog = false
                model.requestDuplicateMoves(plan, actions.approveDuplicates)
            }) { Text("Solicitar movimento") }
        },
        dismissButton = { TextButton(onClick = { dialog = false }) { Text("Voltar") } }
    )
}

@Composable
internal fun DatesScreen(model: BridgeModel, actions: Actions) {
    Text("Compare a data registrada com o Takeout, quando disponível. Salve uma cópia corrigida de um JPEG compatível sem alterar o original.")
    OutlinedButton(onClick = actions.selectDate, enabled = !model.busy) { Text("Selecionar foto") }
    val review = model.datePreview ?: return
    Text("Na foto: ${review.original}")
    review.takeout?.let { Text("Takeout: $it") }
    review.evidence?.let { Text(it) }
    var input by remember(review.identity.sha) { mutableStateOf("") }
    OutlinedTextField(input, { input = it }, label = { Text("Data explícita, ex. 2020-10-11T15:39:09-03:00") })
    OutlinedButton(onClick = { model.proposeDate(input) }, enabled = input.isNotBlank() && !model.busy) {
        Text("Verificar prévia")
    }
    model.dateProposal?.takeIf { runCatching { java.time.OffsetDateTime.parse(input.trim()) }.getOrNull() == it.date }?.let {
        Text("A cópia receberá ${it.date}. Confira se hora, fuso e precisão estão certos.")
        Button(onClick = actions.saveDate, enabled = !model.busy) { Text("Salvar cópia corrigida") }
    }
}
