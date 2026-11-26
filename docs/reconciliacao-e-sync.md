# Sincronização, duplicatas e datas no GalleryBridge 0.4.1

A revisão parte do [PR de Motion Photos](https://github.com/igorcv88/GalleryBridge/pull/2). Os casos do handoff de 26/11 são evidência operacional. Álbuns, contas, URIs, nomes privados, filas e bancos reais não foram incorporados às fixtures.

## Estados observáveis

O diagnóstico lê somente um banco compatível e verifica o perfil do agente. Usa modo SQLite somente leitura, consulta consistente e timeout. Se o esquema mudou, houver mais de um slot ou o acesso falhar, a tela mantém a observação indeterminada. O app lê o timestamp nativo quando disponível. A comparação de tentativas usa um conjunto completo de até 16 identidades temporárias de fila. Os IDs não são gravados nem exportados. Eventos de ciclo precisam ser recentes e posteriores ao início observável do processo. O relatório mostra agregados sem logs brutos.

A tela separa drenagem, backoff, erro transitório, reconciliação ainda pendente e avanço do último sucesso. A redução de contagem mede a fila. Ela não certifica sucesso de cada operação. O fluxo de recuperação abre a Galeria Samsung para solicitar a operação nativa. Não limpa commits, escreve em bancos privados, reinicia serviços ou altera restrições de sistema. Dirty permanece separado de elegibilidade de backup, que não é calculada nesta revisão.

## Duplicatas

A seleção pode reunir arquivos de pastas diferentes. A revisão oferece somente JPEGs estáticos de bytes exatamente iguais e até 48 alvos exibidos por aprovação. O app não usa RGB aproximado para escolher alvos. Favoritos, formatos desconhecidos, XMP, movimento e revisões contraditórias ficam protegidos. O plano inclui caminho, URI de volume, bytes, SHA, geração e estado das cópias. O executor pede consentimento de escrita, reabre cada item e registra a intenção antes da alteração. Confere origem, destino, SHA e sobrevivente após a alteração. Incerteza depois de uma tentativa encerra novas escritas até a revisão do diário. Um desaparecimento local não é evidência de exclusão remota.

O app move para uma pasta de revisão explícita. Não percorre a biblioteca de um provedor cloud-only, não restaura em massa para caminhos antigos e não altera a conta ou o álbum de nuvem. A organização em `DCIM/Archive` e a reserva de `Restored` continuam decisões separadas da remoção de duplicatas.

## Datas

Uma divergência como 1971 no Takeout e uma data moderna no EXIF é exibida como conflito entre fontes. O app exige uma data explícita com offset e não deduz fuso pelo relógio do celular. Para JPEGs simples, escreve somente os campos TIFF ASCII existentes e com espaço compatível. Preserva bytes fora dos campos autorizados, verifica precisão e recusa estruturas sem prova de independência, como MakerNotes, miniaturas com diretório próprio, aliases de offsets, XMP, HEIC e anexos de movimento. Salva uma nova cópia mediante escolha de destino, mantém o original e verifica os bytes do arquivo salvo. Edição no mesmo item remoto e confirmação de sincronização permanecem fora da capacidade comprovada.

## Correspondência da matriz

| Grupo | Cobertura desta revisão |
| --- | --- |
| S01–S06, S11 | Casos JVM de drenagem, identidade de retry, pausa sobre sucesso antigo, qualidade temporal, estado vazio/dirty e avanço do sucesso. O volume de dados e os eventos do agente são sintéticos. |
| S07–S10, S12 | Limites de perfil, sinal ausente, relógio, leitura restrita e recuperação via Galeria. Integração do agente Samsung precisa de aparelho autorizado. |
| M01–M12, D01–D05 | Agrupamento exato, diário, restrições de identidade, movimento por item, staging Motion e rechecagem após a lixeira. Comparação RGB, walled gardens, wave de 1.000 movimentos e identidade cloud-only ficam pendentes. |
| T01–T04, T07–T10 | Proveniência explícita, patch ASCII LE/BE, aliasing, precisão e cópia distinta. Edição do objeto original, warnings do ExifTool, retomada remota e suporte geral a auxiliares permanecem pendentes. |

O teste de lixeira usa linhas sintéticas de propriedade do APK e reconcilia o efeito real no MediaStore após uma mutação de fixture. Isso exercita o diário e a verificação do efeito. A interação de aceitação com o diálogo nativo e a exclusão/recuperação no Galaxy e Google Fotos exigem validação própria.
