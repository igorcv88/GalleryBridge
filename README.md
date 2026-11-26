<p align="center">
  <img src=".github/assets/gallerybridge-banner.svg" alt="GalleryBridge" width="100%" />
</p>

<p align="center">
  <img alt="Android 10+" src="https://img.shields.io/badge/Android-10%2B-3DDC84?logo=android&amp;logoColor=white" />
  <img alt="Status: experimental" src="https://img.shields.io/badge/status-experimental-0e7490" />
  <a href="https://github.com/igorcv88/GalleryBridge/actions/workflows/android.yml"><img alt="CI" src="https://img.shields.io/github/actions/workflow/status/igorcv88/GalleryBridge/android.yml?branch=main&amp;label=CI" /></a>
  <a href="https://github.com/igorcv88/GalleryBridge/actions/workflows/release.yml"><img alt="Release build" src="https://img.shields.io/github/actions/workflow/status/igorcv88/GalleryBridge/release.yml?branch=main&amp;label=release" /></a>
  <a href="https://github.com/igorcv88/GalleryBridge/releases/latest"><img alt="Latest release" src="https://img.shields.io/badge/download-latest-0e7490?logo=github" /></a>
  <a href="https://github.com/igorcv88/GalleryBridge/releases"><img alt="Downloads" src="https://img.shields.io/github/downloads/igorcv88/GalleryBridge/total" /></a>
  <a href="https://github.com/igorcv88/GalleryBridge/stargazers"><img alt="Stars" src="https://img.shields.io/github/stars/igorcv88/GalleryBridge?style=flat" /></a>
  <a href="LICENSE"><img alt="Direitos reservados" src="https://img.shields.io/badge/licen%C3%A7a-direitos%20reservados-lightgrey" /></a>
</p>

# GalleryBridge

**Migração do Google Takeout, reconciliação de mídia e diagnóstico da sincronização da Samsung Gallery.**

O GalleryBridge analisa os arquivos ZIP do Google Takeout, identifica cópias idênticas e permite revisar datas, outros metadados e favoritos antes de preparar a migração. O aplicativo principal funciona em Android 10 ou posterior, sem root. A sincronização com o Google Fotos é feita pela Samsung Gallery nos aparelhos e contas em que o recurso está disponível.

[Versões](https://github.com/igorcv88/GalleryBridge/releases) · [Problemas](https://github.com/igorcv88/GalleryBridge/issues) · [Licença](LICENSE)

## Estado do projeto

A versão 0.4.1 está em teste. Além da ingestão do Takeout, inclui revisão de Motion Photos, duplicatas locais, cópias com datas reparadas e diagnóstico opcional do MediaSync. Os testes JVM e instrumentados em AOSP cobrem contratos locais; a validação ponta a ponta em Galaxy e Google Fotos continua pendente.

> [!IMPORTANT]
> A sincronização pode enviar a mídia inteira, mesmo quando uma duplicata não fica visível. Um lote de 20 GB pode consumir cerca de 20 GB de upload, ou mais se houver novas tentativas. Desative os dados móveis para a sincronização na Samsung Gallery e faça o primeiro piloto em Wi-Fi. As restrições de rede do GalleryBridge valem para o próprio app.

## O que você pode fazer

| Se você quer… | Use o GalleryBridge para… |
|---|---|
| Conferir o que veio no Takeout | Analisar os ZIPs disponíveis e ver quais partes terminaram de ser verificadas. Compare também a lista de partes com o manifesto do Takeout. |
| Evitar criar cópias idênticas | Comparar o conteúdo dos ZIPs entre si e com os arquivos locais que você selecionar. Uma cópia alterada pode aparecer como outro item. |
| Recuperar datas e outros metadados | Revisar data, localização, legenda, orientação e pasta de destino. As opções dependem do formato e se aplicam às novas cópias compatíveis. |
| Recuperar favoritos históricos | Revisar os favoritos indicados no Takeout e marcar arquivos locais correspondentes, com confirmação do Android 11 ou posterior. |
| Conferir o resultado | Separar a presença local, o favorito no aparelho e sua própria conferência do item no Google Fotos. |
| Recompor Live Photos | Identificar pares pelo identificador interno, preparar Motion JPEG e publicar com verificação dos componentes; validar a reprodução no Galaxy. |
| Revisar duplicatas locais | Agrupar JPEGs estáticos de SHA-256 idêntico, escolher o sobrevivente e mover redundâncias confirmadas para uma pasta de revisão. |
| Corrigir datas | Comparar a data do Takeout e do EXIF; salvar uma nova cópia JPEG compatível após informar data e offset explícitos. |
| Diagnosticar sincronização | Com root autorizado e perfil reconhecido, observar fila, backoff, erros e último sucesso do MediaSync, sem escrever no banco do agente. |

## Instalação e primeiro piloto

1. Instale o APK oficial quando estiver disponível em [Versões](https://github.com/igorcv88/GalleryBridge/releases). Se você recebeu um APK de teste diretamente do projeto, use um emulador ou uma instalação separada de teste. Um APK assinado com outra chave não atualiza a instalação anterior. Preserve qualquer catálogo que precise consultar.
2. Abra o [Google Takeout](https://takeout.google.com/), selecione **Google Fotos** e peça uma exportação em ZIP. Guarde todas as partes. Para experimentar sem usar fotos pessoais, selecione o [ZIP de demonstração](fixtures/Takeout-sintetico.zip).
3. No GalleryBridge, abra **Origem**, selecione os ZIPs e toque em **Conferir ZIPs e retomar análise**. Dá para selecionar arquivos locais ou arquivos do Drive, quando o Drive estiver disponível no seletor do Android. Se uma parte do Drive falhar, baixe aquela parte e selecione a cópia local.
4. Em **Metadados**, revise os itens do piloto e exclua da publicação o que deve ficar privado. Vídeos exigem aprovação individual. Se escolher o ZIP de demonstração, a prévia deve oferecer duas cópias e manter o item da pasta protegida fora do lote.
5. Em **Lotes**, confira a lista e aprove um lote pequeno. Mantenha o app aberto durante a preparação. Se sair da tela, você pode retomar depois. As cópias aparecem nas pastas `GalleryBridge_NNN` dentro de **Pictures** ou **Movies**.
6. Se o recurso estiver disponível no seu Galaxy, confira a conta e o álbum em **Samsung Gallery → Configurações → Sincronizar fotos e vídeos**. Compare os itens no Galaxy e no Google Fotos antes de preparar os outros lotes.

## Como recuperar favoritos

Abra **Metadados → Conferir favoritos históricos**, revise a lista e selecione os itens desejados. Confirme a mudança no diálogo do Android. O GalleryBridge verifica o favorito local depois da ação. Se o app fechar durante o pedido, volte a **Metadados** e use **Conferir pedido de favoritos interrompido**.

Para um item que só está na nuvem, abra o item existente na Samsung Gallery e baixe-o, se essa opção estiver disponível. Em **Lotes**, use **Conferir MediaStore** se o arquivo constava em um lote anterior. Em seguida, selecione o arquivo baixado em **Origem → Comparar arquivos locais por SHA-256** e volte à prévia de favoritos. Quando os arquivos diferem ou há mais de um destino possível, o caso fica em revisão.

A marcação confirmada no Android vale para o arquivo local. Confira o mesmo item no Google Fotos, na conta correta, para saber se o favorito também aparece na nuvem.

## Live Photos e Motion Photos

Na seção **Motion**, analise os pares após a ingestão dos ZIPs. A recomposição copia a foto e o vídeo sem recodificação, com staging e hashes independentes. Revise a publicação e teste a reprodução com áudio na Samsung Gallery. O app permite comparar um sobrevivente e uma possível redundância local, solicitar movimentação ou envio à lixeira com consentimento e reconciliar recibos interrompidos. A operação depende de evidência suficiente e da confirmação de que o redundante não está sincronizado. Itens exclusivamente na nuvem devem ser conferidos pela Gallery; não há exclusão remota automática.

## Duplicatas e reparo de datas

Na seção de duplicatas, selecione arquivos locais ou revise correspondências do catálogo. A implementação atual oferece apenas JPEGs estáticos de bytes exatamente iguais, até 48 alvos por aprovação, protegendo favoritos, movimento, XMP e formatos desconhecidos. As movimentações usam MediaStore, consentimento e diário, sem exclusão permanente. Não equivale a deduplicação global cloud-only nem a comparação por pixels aproximados.

Na seção de datas, selecione uma foto, confira o conflito com o Takeout, informe data e fuso e salve uma **nova cópia**. O original não é alterado; JPEGs com estruturas não verificadas ficam bloqueados. Não há reparo em massa de datas diretamente no Google Fotos.

## Diagnóstico do MediaSync

A leitura opcional com root usa perfil reconhecido do agente Samsung e banco somente leitura. O app distingue progresso da fila, backoff, erro transitório e avanço do último sucesso. A ausência local, um cache ou uma fila vazia não confirmam exclusão, backup ou integridade remota. O fluxo de recuperação abre a Samsung Gallery; não remove commits, força estados, reinicia serviços nem ignora limites térmicos. O relatório exportado contém dados agregados e sanitizados. Consulte [Reconciliação e sync](docs/reconciliacao-e-sync.md) e [Motion Photos](docs/motion-reconciliation.md).

## Datas, álbuns e privacidade

- Revise fotos antigas e de viagens antes de publicar. Uma data sem informação de fuso fica como horário local, e a Samsung Gallery pode ordenar a mídia por uma data diferente da indicada pelo Takeout.
- A pasta escolhida vale para a cópia local. A associação com vários álbuns do Google Fotos exige conferência separada.
- A preparação de Motion JPEG é experimental. Teste a reprodução em uma pasta fora da sincronização antes de incluí-la no piloto.
- O app mantém o catálogo no aparelho, trabalha com os arquivos que você seleciona e preserva os ZIPs originais. Para pedir ajuda, exporte o resumo em **Relatório** e confira o que pretende compartilhar.
- A liberação automática de espaço fica bloqueada no GalleryBridge. Excluir um item sincronizado pode removê-lo da nuvem e de outros aparelhos. Guarde os originais e confirme os itens na nuvem antes de usar a opção de liberação de espaço da Samsung.

## APKs, CI e releases

- [Releases](https://github.com/igorcv88/GalleryBridge/releases/latest): APK oficial assinado e checksum SHA-256, quando disponível.
- [Actions](https://github.com/igorcv88/GalleryBridge/actions/workflows/android.yml): testes do motor e do app, lint, Detekt, builds e testes instrumentados em API 30 e 37.0. Commits enviados ao repositório têm APK assinado após os checks, disponível por um dia. PRs externos não recebem a chave privada.
- Após merge de PR em `main`, o workflow de release repete os checks e publica diretamente em Releases. O canal estável usa a tag móvel `latest`; a label `release:prerelease` no PR antes do merge solicita um pré-release separado. Não são enviados artefatos de Actions no workflow de release.

O APK oficial exige a mesma chave de assinatura para atualizar uma instalação existente. Os antigos APKs de teste podem ter outra chave. Não desinstale antes de preservar seus dados e catálogo. Veja [a configuração de CI e assinatura](docs/ci-release.md).

## Ajuda

Ao abrir um [relato de problema](https://github.com/igorcv88/GalleryBridge/issues), inclua o modelo do Galaxy, a versão do Android e da One UI, a versão do GalleryBridge e a etapa em que ocorreu a falha. Evite anexar fotos pessoais, ZIPs reais ou links privados do Google Fotos.

O GalleryBridge é um projeto independente, sem vínculo com Samsung ou Google. O módulo experimental MediaSync para LSPosed é separado do app principal e tem [requisitos próprios](mediasync-experimental/README.md).

## Licença

O código do GalleryBridge está disponível para consulta pública, com direitos reservados. Você pode citar o projeto com crédito e link para este repositório. A exceção de fork se limita ao que os Termos de Serviço do GitHub permitem. Desvincular um fork para torná-lo independente, republicar o código ou reutilizá-lo em outro projeto exige autorização prévia e expressa de Igor (igorcv88). Consulte a [licença completa](LICENSE) e os [avisos de terceiros](THIRD-PARTY-NOTICES.md).

<details>
<summary>Compilação e documentação do projeto</summary>

Use JDK 21, Android SDK 36 e Build Tools 36.0.0.

    ./gradlew :engine:test :app:testDebugUnitTest :app:lintDebug detekt :app:assembleDebug
    ./gradlew :app:connectedDebugAndroidTest
    apksigner verify --verbose --print-certs app/build/outputs/apk/debug/app-debug.apk

- [Compatibilidade por função](docs/support-matrix.md)
- [Reconciliação e protocolo de teste](docs/reconciliation-0.3.1.md)
- [Arquitetura](docs/architecture.md)
- [Privacidade](docs/privacy-and-threats.md)
- [Validação](docs/validation.md)
- [Origem do código](docs/provenance.md)

</details>

## Live Photos e fotos com movimento

1. Analise os ZIPs em **Origem**. Abra **Motion Photos** e toque em **Analisar pares**. O app procura o identificador interno da foto e do vídeo. Pares incompletos, ambíguos ou protegidos ficam em revisão.
2. Se você já tiver a versão histórica com movimento, selecione-a antes de recompor. Quando o app comprovar que ela contém os mesmos componentes do ZIP, o par selecionado fica fora da recomposição nessa revisão.
3. Toque em **Recompor pares desta página**. As cópias ficam no app até você aprovar **Publicar cópia**. A recomposição mantém a imagem e o vídeo originais, sem recodificar. Abra a cópia publicada e teste movimento e áudio na Galeria.
4. Para revisar uma redundância, escolha primeiro a foto que deve permanecer. Depois escolha a recomposta, a estática ou o vídeo avulso que deseja comparar. Se o item estiver só na nuvem, use o download do próprio app de origem, quando disponível. O GalleryBridge compara o arquivo escolhido e nunca faz download em massa.
5. Para um redundante exclusivamente local, confira os atributos e a reprodução da foto que permanece. Depois use **Mover para o álbum GalleryBridge_Revisao** quando o GalleryBridge tiver criado a cópia, ou solicite **Enviar redundante à lixeira**. O Android pede consentimento. A tela acompanha o resultado no aparelho e oferece restauração da última ação compatível.

JPEG compatível pode ser recomposto. O GalleryBridge lê o formato Motion Photo JPEG com XMP e uma variante Samsung SEF de entrada única. HEIC, HDR com dados auxiliares e variantes que o app não consegue verificar ficam em revisão. As origens no Takeout são mantidas. As cópias temporárias e verificadas usam espaço no aparelho.

Uma mídia sincronizada pode sumir do Google Fotos e de outros aparelhos quando for excluída. Use o caminho nativo da Galeria para itens na nuvem e preserve o item se houver dúvida sobre a associação. O app não trata a lixeira local como confirmação de exclusão na nuvem.

Se você usa **DCIM/Archive** para o acervo consolidado, mantenha a organização separada da tela de revisão de duplicatas. Preserve **Restored** para downloads individuais e **Camera** para capturas. O feed reúne as mídias de várias pastas. Remover um álbum ainda preenchido pode remover as mídias que ele contém.


## Sincronização, duplicatas e datas

- Em **Avançado**, abra a Galeria para solicitar a sincronização nativa em Wi-Fi. Em perfis reconhecidos, o diagnóstico opcional mostra fila, tentativas agendadas, registros pendentes e o horário de sucesso observado. Para medir redução da fila ou comparar tentativas, faça uma segunda leitura depois de alguns minutos. Fila vazia e o fim do indicador de progresso não confirmam sincronização com o Google Fotos.
- Em **Duplicatas**, selecione as fotos locais ou use as correspondências locais já registradas no catálogo. O GalleryBridge forma grupos de JPEGs estáticos com bytes idênticos, mantém uma cópia por grupo e protege favoritos. O app mostra até 48 cópias por aprovação. Confirme as versões e os álbuns antes de pedir o movimento para **Pictures/GalleryBridge_Revisao_Duplicatas**. O Android precisa permitir a escrita. Se o app fechar durante o movimento, confira o diário antes de repetir a seleção. A revisão não apaga arquivos nem decide se eles foram excluídos do Google Fotos.
- Em **Datas**, escolha uma foto. O app mostra a data do arquivo e a do Takeout quando encontra a mesma mídia por conteúdo. Para JPEGs compatíveis, digite a data com o fuso e verifique a prévia antes de salvar uma cópia corrigida. O original permanece acessível. HEIC, Motion Photos e estruturas EXIF que não podem ser verificadas ficam em revisão.
