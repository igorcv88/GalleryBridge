# Favoritos históricos e retomada no GalleryBridge 0.3.1

A branch `gallerybridge/reconciliation-recovery` deriva de `c9880ac` (0.2.0) e mantém `02bedb4` (0.1.0) no histórico. O código de favoritos do 0.3.0 foi reconstruído a partir da revisão anterior e do APK assinado. O esquema 3 do banco preserva o formato da tabela de observações usado naquele APK. A árvore Git original do 0.3.0 e a chave privada de teste daquela rodada não foram recuperadas. Use o novo APK em um emulador ou instalação de teste limpa. Não desinstale uma cópia que contenha um catálogo que precise preservar.

## Da origem até a verificação

1. Analise todos os ZIPs disponíveis em **Origens**. Compare os arquivos locais, quando houver, para excluir correspondências exatas dos próximos lotes.
2. Em **Metadados → Conferir favoritos históricos**, revise as indicações do Takeout e os destinos. Indicações verdadeiras e falsas para a mesma identidade aparecem como conflito. Um destino local deve ter SHA-256, tamanho e URI compatíveis.
3. Selecione até 100 alterações de cada vez e aprove a solicitação do Android. A prévia usa somente leitura. A confirmação lê novamente a indicação local e os bytes. Quando um pedido é interrompido, a retomada registra o estado observado sem atribuir consentimento que não pôde recuperar.
4. Abra o item em **Metadados → Favorito** para reler o MediaStore, consultar o MediaSync em **Diagnóstico avançado** ou conferir o item original no Google Fotos. A conferência visual requer um único ID em link confiável do Takeout. O app descarta parâmetros de conta e fragmentos. Escolha a conta correspondente no aplicativo ou navegador que abriu o link.
5. A consulta individual do MediaSync exige root concedido, versão `1.6.09.34` e código `160900034` do Samsung Storage Agent, e um destino local único e íntegro no volume principal. Usa o ID do MediaStore e caminho exato para consultar contagens no SQLite em modo somente leitura. Um commit limpo é evidência do cache local do SDK. A confirmação visual permanece uma observação do usuário. Nenhuma dessas evidências libera espaço ou resolve pendências técnicas de nuvem.

## Favorito com mídia disponível só na nuvem

Abra o item já existente na Samsung Gallery e use a função de download do próprio aplicativo, se ela estiver disponível. Em **Lotes**, confira o MediaStore para registrar a ausência do recibo anterior. Em **Origens**, escolha o arquivo baixado em **Comparar arquivos locais por SHA-256** e então refaça a prévia de favoritos. A aplicação exige que o original exportado corresponda exatamente ao novo URI e que o Android aceite a solicitação. Se o download alterar os bytes ou houver mais de um destino possível, deixe em revisão. Importar o ZIP novamente pode criar um item independente e não é uma operação de restauração.

## Protocolo físico autorizado, com fixtures sintéticas

| Rota | Preparação e evidência esperada |
|---|---|
| Favorito já associado | Use conta de teste e fixture com link original identificável. Registre estados inicial MediaStore, Gallery, SDK opcional e Google Fotos em sessão remota independente. Aplique uma vez com consentimento, repita as leituras e compare o mesmo item em sessão independente. Veja se já estava marcado antes de atribuir propagação. |
| Cloud-only | No ambiente de teste, confirme a identidade remota antes e depois da remoção local pelo recurso correto da Samsung. Baixe pelo próprio item, compare o SHA e verifique se a mesma identidade e o número de itens remotos ficaram estáveis. Termine com o protocolo de favorito. |
| GPS, legenda, orientação, álbum ou data de item já sincronizado | Edite um campo por vez em superfície nativa autorizada. Compare bytes e atributos locais, o estado de associação e o valor remoto no mesmo item. Monitore duplicações. O GalleryBridge só grava esses atributos nas cópias de preparação até se comprovar uma rota de escrita segura no item existente. |
| LSPosed | Use somente o APK experimental separado e aparelho de teste autorizado com versão exata do agente. Compare desativado, observação e um único controle ativo a cada vez. Registre motivos reais, progresso do mesmo lote, status térmico real, headroom e energia. No perfil desconhecido, espere recusa. Suspenda o teste ao perder margem térmica. |

A conferência de um item não resolve as dez pendências históricas. Os 106 favoritos do estudo são referência da fixture, sem execução sobre contas ou mídias reais.
