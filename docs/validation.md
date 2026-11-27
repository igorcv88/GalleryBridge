# Matriz de validação

| Cenário | Cobertura |
|---|---|
| 44 ZIP64, 49.045 identidades únicas, caminhos duplicados e sidecars | Teste JVM sintético com bytes pequenos. Tamanho real de 200 GiB e Drive ao vivo ficam fora da medição |
| ZIP-slip, symlinks, .nomedia, CRC divergente e truncamento | Testes JVM |
| Rede tarifada, temperatura severa, carga mínima e causas nativas incertas | Regras JVM e limites visíveis na UI |
| Offset, EXIF de parede, Keys, QuickTime e IDs Live Photo repetidos | Testes JVM e fixture MP4 estrutural. Amostras Apple reais ainda precisam de validação |
| Favoritos contraditórios e localização com sentinel | Teste de leitura JVM. Consentimento público e efeito Samsung dependem de validação em aparelho |
| Fila externa, mudança de entidade/path/tipo, ausência após Free up | Regras do motor JVM. Agent indisponível neste APK |
| Lote sem orçamento, checkpoint reaberto, cópia parcial, verificação MediaStore, histórico de dez pendências | Fixture instrumentada com JPEGs criados no teste |
| Conta revogada no Drive, morte real do processo em todas as janelas e export de centenas de GB | Cobertura parcial de código e fixtures simuladas. Testes reais pendentes |
| Sync Samsung, exclusão sincronizada e Free up | Fluxo manual, condicionado à conta de teste e aparelho autorizado. A ausência local não é prova de exclusão remota |

O workflow executa testes JVM, MediaStore instrumentado em API 30, lint, Detekt e conferência da assinatura. O PR inicial documenta as verificações pendentes. O comportamento do agente Samsung depende de validação separada.

## Rodada 0.2

O Android 11 API 30 confirmou o teste de retomada do APK 0.1.0 em execução única após o boot. A nova suite instrumentada acrescenta a migração do banco, a verificação dos valores escritos em uma cópia JPEG publicada e a confirmação de favoritos pelo diálogo real do Android. O relatório da execução é fornecido no pacote de revisão. Testes JVM também exercitam flags contraditórias e a estrutura XMP e o payload de movimento.
