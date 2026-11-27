# Suporte por função

| Função | API / permissão | Estado |
|---|---|---|
| ZIPs locais e Drive | API 29+, SAF leitura persistida | Múltipla seleção. Drive depende do provedor. Cache de uma parte para provedor sem seek |
| Catálogo Takeout | API 29+, banco privado | ZIP64, CRC/SHA, dedup e proveniência. Sidecar por nome completo inequívoco no mesmo diretório e parte. Cobertura pelo manifesto é manual |
| Comparar mídia local | API 29+, arquivos selecionados no SAF | SHA exato dos bytes disponíveis no URI escolhido |
| Publicação | API 29+, MediaStore | Cópia da qual o app é proprietário, reaberta e verificada |
| Data | API 29+, inserção em MediaStore | Preservação padrão. Cópias de imagem podem receber EXIF com offset. Publicação relê a data indexada. Escrita pós-publicação em revisão |
| Live Photo | Parser QuickTime mdta, Apple EXIF/XMP limitado | ID inequívoco ou revisão. Exportação de Motion JPEG experimental. Reprodução Samsung e HEIC em validação |
| Favorito | API 30+, URI de mídia e consentimento do Android | Proveniência por parte e entrada, SHA, consentimento e releitura. Estado local, cache MediaSync e observação manual do Google Fotos separados |
| GPS, descrição, álbum, orientação e edição | EXIF e Takeout em imagens compatíveis | GPS, legenda e orientação em cópias com readback. Álbum como pasta local. Edições multimídia em revisão |
| Energia e rede | API 29+ status térmico, API 30+ headroom, ACCESS_NETWORK_STATE | Política apenas do próprio helper |
| Samsung Cloud Sync | Galaxy/One UI 9 onde houver rollout, contas Samsung e Google | Sincronização nativa. Contadores e associação do SDK quando o perfil root estiver disponível. Sem confirmação independente |
| Google Fotos histórico | Library API, Data Portability, Picker | Library limitada a conteúdo criado pelo app. Picker exige escolha. Escopo de portabilidade de biblioteca Photos não localizado |
| Download nativo de item cloud-only | Samsung Gallery e seleção SAF | Fluxo manual com reidentificação por SHA. Preservação da associação nativa ainda precisa de teste físico |
| Free up | Recurso Samsung e conferência por item | Gate automático bloqueado. Auditoria local disponível |
| Root e LSPosed | Perfil autorizado e reconhecido | Perfil root limitado a contagens e associação somente leitura. APK LSPosed experimental separado da instalação padrão, sem validação física |
| Exportação | SAF, destino escolhido | Resumo JSON sanitizado e catálogo JSONL v1 opcional com SHA e proveniência. Importação de ledger pendente |

## Fontes verificadas em 25/11/2026

- [Samsung Newsroom](https://news.samsung.com/us/samsung-gallery-makes-it-easy-to-access-photos-videos-across-supported-galaxy-devices/)
- [Google Photos Library API](https://developers.google.com/photos/library/guides/list)
- [Campos retornados pelo Picker do Google Fotos](https://developers.google.com/photos/picker/reference/rest/v1/mediaItems?hl=pt-br)
- [Google Photos Picker e Library API](https://developers.google.com/photos/overview/about)
- [Escopos Google Data Portability](https://developers.google.com/data-portability/user-guide/scopes)
- [Android SAF](https://developer.android.com/training/data-storage/shared/documents-files)
- [Android MediaStore](https://developer.android.com/training/data-storage/shared/media)
- [Consentimento de favoritos no Android](https://developer.android.com/reference/android/provider/MediaStore#createFavoriteRequest(android.content.ContentResolver,%20java.util.Collection,%20boolean))
- [Android Thermal APIs](https://developer.android.com/games/optimize/adpf/thermal)
