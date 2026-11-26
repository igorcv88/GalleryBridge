# Reconciliação histórica e verificação

| Função | Rota | Evidência e limite |
|---|---|---|
| Favoritos do Takeout | SHA, JSON associado por arquivo e pasta inequívocos, agregação de flags por identidade | Flag positiva vira candidato. Positivo e negativo explícitos exigem revisão. Flag ausente ou negativa não desfavorita automaticamente |
| Contagem histórica de 106 | Fixture sintética no motor de políticas | Não é afirmação de que todo export contém 106 ou de que todos foram aplicados |
| Favoritar mídia local | Android 11+, URI MediaStore, SHA original ou derivado verificado, consentimento do sistema | Prévia somente leitura. Ida e volta pelo MediaStore verificadas no emulador. A propagação Samsung é independente |
| Encontrar destino preexistente | SAF com grant de leitura, MediaStore.getMediaUri quando disponível, rehash do URI de destino | Sem URI equivalente, acesso revogado, bytes alterados ou mais de um destino aceito, permanece em revisão |
| Favoritos cloud-only | Reobter o mesmo objeto pela função da Samsung Gallery e comparar o arquivo restaurado | A raiz do provider privado rejeitou a consulta na migração anterior. A restauração e o vínculo precisam ser conferidos no Galaxy. Não há escrita nesse provider |
| Data de captura | EXIF com offset em cópias de imagens suportadas e solicitação de DATE_TAKEN na publicação | Releitura EXIF e estado observado de índice separados. Não inventa offset. Não reescreve QuickTime nem força Keys |
| GPS, legenda e orientação | AndroidX ExifInterface em cópias JPEG/PNG/WebP aceitas | Releitura verificável das tags. O destino local pode ignorar uma tag ou escolher outra fonte de apresentação |
| Álbum | Caminho da nova cópia MediaStore | Verifica a pasta local. Vários álbuns para a mesma mídia exigem escolha. Sem mutação de álbuns Photos |
| Live Photos | Identificador interno inequívoco mais MIME e integridade de ambos os componentes | Exportação experimental JPEG + MP4/MOV com XMP e validação do vídeo. HEIC Motion Photo e reprodução Samsung/Photos aguardam validação |
| Diagnóstico | APIs públicas e perfil root separado por ação de UI | Os contadores do SDK são locais, somente leitura e desidentificados. Sem LSPosed, alterações de commits ou políticas de terceiros |
| Free up e exclusão sincronizada | Fluxo nativo condicionado a prova suficiente | Gate automático bloqueado. Excluir pelo fluxo comum da Gallery pode incluir nuvem e outros dispositivos |

## Consentimento e recuperação

A prévia de favoritos só consulta o catálogo, os bytes autorizados e IS_FAVORITE. A etapa de aplicação recalcula SHA e revalida o URI antes de abrir createFavoriteRequest. A intenção fica registrada para recuperação após morte do processo. Depois do retorno, uma segunda leitura compara SHA, tamanho e flag. Um resultado observado sem callback recuperado conta como estado atual, sem atribuição indevida de autoria. Todos os resultados remotos continuam sem confirmação independente.

O banco migra de v1 para v2 sem apagar fontes, lotes ou recibos. A cópia derivada grava SHA e tamanho de saída antes de publicar. O arquivo no ZIP continua sendo a referência. A revisão do mesmo campo pode selecionar conflitos temporais equivalentes por origem, tipo e período, com exceções por item. Grupos acima de 200 itens exigem revisão em escopo menor. A escrita efetiva ocorre antes da primeira publicação para evitar substituir ou duplicar uma mídia já sincronizada. GPS, legenda e orientação aplicados à cópia não constituem prova de propagação.

## Observação do agente Samsung

O perfil inicial reconhece somente com.samsung.android.agent.storage 1.6.09.34, o caminho de banco histórico do usuário Android 0 e /system/bin/sqlite3. Exige confirmação no app e a autorização do gerenciador de root. A leitura usa opção -readonly e PRAGMA query_only. Versão, executável, acesso, caminho ou esquema diferentes produzem motivo específico. A consulta por item requer propriedade do GalleryBridge, SHA, URI, ID MediaStore, pasta e nome determinístico antes de ler a linha correspondente. A resposta é agregada. A rota não coleta tokens, chaves de conta, mensagens RPC nem cargas que contenham dados pessoais. TRANSIENT_ERROR, autenticação e quota permanecem sem atribuição quando somente contagens estão disponíveis.

O helper reduz o ritmo local em condição térmica moderada e interrompe em condição severa. Lotes exigem Wi-Fi não tarifada, reserva de disco e condições de bateria escolhidas. Os bytes previstos da Gallery correspondem ao volume total antes de eventual dedup, com possibilidade de novas tentativas. Os contadores do UID do assistente não medem o tráfego da Gallery.

## Origem da portabilidade

Os scripts takeout_favoritos.py e takeout_favoritos_aplicar.py cruzavam URLs e snapshots privados no Termux. A implementação padrão usa flags oficiais do export, URI público e hashes exatos, com parada em caso de conflito. A rota antiga em que --check chegava à escrita não foi portada. O modelo de proteção do caso de 45.689 associações no lote 005 e dez pendências continua conservador. Essas pendências não são classificadas como resolvidas por resultados sintéticos.

## Referências

- [MediaStore e createFavoriteRequest](https://developer.android.com/reference/android/provider/MediaStore)
- [AndroidX ExifInterface](https://developer.android.com/reference/androidx/exifinterface/media/ExifInterface)
- [Motion Photo 1.0](https://developer.android.com/media/platform/motion-photo-format)
- [Samsung Brasil, integração Gallery e Google](https://www.samsung.com/br/support/mobile-devices/sync-gallery-to-the-cloud-with-a-google-account/)
