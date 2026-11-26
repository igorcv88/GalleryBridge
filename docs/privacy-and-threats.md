# Privacidade e modelo de ameaças

O app acessa os arquivos escolhidos no seletor do Android. Não solicita conta, senha ou token Google, permissão global de armazenamento ou permissão INTERNET. O Drive pode baixar um arquivo selecionado nele. Um relatório salvo em uma pasta de nuvem poderá ser sincronizado pelo provedor escolhido. A Gallery realiza sua própria sincronização.

O catálogo privado guarda SHA, caminhos, metadados e possíveis coordenadas. O backup Android está desabilitado. O relatório normal contém contagens e estados. O usuário pode acrescentar diagnósticos agregados. Desinstalar remove os dados privados do helper e mantém as cópias compartilhadas.

| Risco | Controle |
|---|---|
| ZIP-slip, symlink e expansão anormal | Validação estrutural, limites e leitura em blocos. Caminho de entrada não é caminho de escrita |
| ZIP parcial ou trocado | CRC e SHA, duas leituras binárias do ZIP para habilitar publicação, nova verificação de SHA/CRC na saída |
| Arquivo local preexistente ou colisão | Posse, token, caminho e URI. Cópia pendente durante a escrita |
| Interrupção e pouco espaço | Intenção transacional, recuperação de URI, retomada reinicia só o arquivo parcial. Reserva antes de copiar |
| Mídia privada | Filtro de diretórios conhecidos, descendentes de .nomedia, bloqueio de SHA restrito e exclusão individual |
| Tráfego tarifado | Wi-Fi não tarifada e previsão por bytes. Troca de rede em chamada bloqueante é observada quando o controle retorna |
| Risco térmico | Status SEVERE ou headroom ≥ 0,90 pausa o helper. Trabalho reduzido em MODERATE |
| Sucesso remoto indevido | Revisão manual, evidência local e vínculo remoto distintos. Ausência física em revisão |
| Exclusão sincronizada | Sem comando de exclusão de mídia. Guia explica remoção bidirecional |
| Assinatura | APK debug de teste. Sem keystore ou credencial no projeto |
