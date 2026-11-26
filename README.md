# GalleryBridge

**Confira sua exportação do Google Fotos e prepare um lote de fotos e vídeos para a Samsung Gallery.**

O GalleryBridge analisa os arquivos ZIP do Google Takeout, identifica cópias idênticas e permite revisar datas, outros metadados e favoritos antes de preparar a migração. O aplicativo principal funciona em Android 10 ou posterior, sem root. A sincronização com o Google Fotos é feita pela Samsung Gallery nos aparelhos e contas em que o recurso está disponível.

[Versões](https://github.com/igorcv88/GalleryBridge/releases) · [Problemas](https://github.com/igorcv88/GalleryBridge/issues) · [Licença](LICENSE)

## Estado do projeto

A versão 0.3.1 está em teste. A análise dos ZIPs, a criação de cópias e a marcação local de favoritos foram exercitadas com arquivos sintéticos. A propagação de favoritos para o Google Fotos e a reprodução de Motion Photos na Samsung Gallery ainda precisam de validação em um Galaxy.

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

## Datas, álbuns e privacidade

- Revise fotos antigas e de viagens antes de publicar. Uma data sem informação de fuso fica como horário local, e a Samsung Gallery pode ordenar a mídia por uma data diferente da indicada pelo Takeout.
- A pasta escolhida vale para a cópia local. A associação com vários álbuns do Google Fotos exige conferência separada.
- A preparação de Motion JPEG é experimental. Teste a reprodução em uma pasta fora da sincronização antes de incluí-la no piloto.
- O app mantém o catálogo no aparelho, trabalha com os arquivos que você seleciona e preserva os ZIPs originais. Para pedir ajuda, exporte o resumo em **Relatório** e confira o que pretende compartilhar.
- A liberação automática de espaço fica bloqueada no GalleryBridge. Excluir um item sincronizado pode removê-lo da nuvem e de outros aparelhos. Guarde os originais e confirme os itens na nuvem antes de usar a opção de liberação de espaço da Samsung.

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
