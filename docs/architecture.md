# Arquitetura e estados

:engine concentra integridade, ZIP64, parsing de data, fontes Takeout, formato, QuickTime e regras de evidência. :app usa Kotlin, Compose, SQLite/WAL, SAF, MediaStore e uma fila serial em ViewModel. O trabalho pausa quando o app sai do primeiro plano e retoma por ação explícita depois de pausa, morte ou reinício.

O pipeline passa por seleção SAF, SHA do ZIP, CRC e SHA por membro, catálogo privado, prévia do lote, staging pendente MediaStore, hash reaberto, publicação e revisão manual na Gallery. A parte passa por SELECTED, READING e VERIFIED. Falhas e pausas têm estados próprios. Partes que não completaram a verificação ficam fora dos lotes. A identidade inclui URI SAF, SHA de ZIP, ordinal, caminho, tamanho e CRC. O agrupamento de mídia usa SHA-256 e mantém as relações com diretórios de origem.

| Tabela do banco v1 | Identidade e uso |
|---|---|
| sources | URI SAF, SHA integral do ZIP, tamanho e estado |
| entries | Fonte + ordinal, caminho, CRC, SHA, metadados, ID Live Photo, privacidade e formato |
| local_match | SHA de mídia local escolhida pelo usuário |
| reviews | Exclusões e autorização de vídeo independente |
| batches | Estado de publicação e revisão visual |
| publication | SHA, token determinístico, fonte, URI, estado local e data observada |
| decisions | SHA + campo, ação, escopo e horário |
| remote_pending | Marcadores históricos não resolvidos |

O banco usa foreign keys e WAL. Versões futuras exigem migrações explícitas. A criação do lote e a reserva dos SHAs ocorrem na mesma transação. O app grava a intenção antes da inserção MediaStore. O retomador exige correspondência única por token, diretório, pacote proprietário e URI registrada, quando existe. IS_PENDING protege arquivos parciais. O app reabre os bytes após publicação antes de gravar E1. Ausência posterior permanece em revisão com SHA reservado.

E0 é integridade da fonte e E1 é hash/publicação locais. Revisão visual é declaração do usuário. E2/E4 exigem fonte autorizada indisponível neste APK. O gate de Free up fica bloqueado. Os contratos em schemas/ foram incluídos como referência. O relatório agregado possui formato próprio com schema_version.

## Evolução

1. Medir volumes reais com Drive, USB e MediaStore. Avaliar download independente para partes sem seek, batching SQLite e limites de I/O.
2. Ampliar sidecars ambíguos, cobertura de manifesto, Apple MakerNote e variantes QuickTime com fixtures verificáveis.
3. Validar o piloto Samsung em conta de teste antes de habilitar gates ou escritas nos campos ainda indisponíveis.
4. Adicionar leitura root somente com perfil da versão do agente, esquema confirmado, autorização e sanitização. O APK atual degrada para UNSUPPORTED.
