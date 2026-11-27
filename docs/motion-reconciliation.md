# Reconciliação de fotos com movimento

Base remota `5cbfe0001c714c3570e48dcdc395fca9dcc39bdc`, PR inicial #1 em rascunho. A base preserva ingestão Takeout, favoritos, metadados, diagnóstico MediaSync opt-in e exportação JPEG. Esta revisão adiciona navegação própria, staging durável, publicação no MediaStore e revisão com consentimento. O commit original de licença segue intacto.

## Formatos e prova

A escrita usa o formato [Motion Photo 1.0 do Android](https://developer.android.com/media/platform/motion-photo-format), sem código de conversores de terceiros. A imagem e o vídeo são copiados sem conversão. O leitor parseia os marcadores JPEG até o EOI e confere o contêiner de vídeo, os limites e os dois hashes. Para Samsung SEF aceita um único campo `MotionPhoto_Data` com offsets fechados entre o EOI e o diretório. Caudas adicionais, múltiplas entradas, XMP adicional e HEIC protegido por estruturas que não são verificadas ficam em revisão. A extração de pistas por `MediaExtractor` e a abertura JPEG com o decodificador Android são checagens locais; reprodução e áudio precisam de validação no aparelho de destino.

Os scripts v15/v22 orientaram identificação e prova de payload, v25/v26/v28 orientaram staging e recibos, v32/v37/v44/v45/v48/v49/v51 motivaram revisão conservadora e v70–v75 motivaram fila sem barreira e revalidação do caminho. Nenhum script histórico integra o app. Os 17 casos do estudo não viram regras em código. A reconciliação usa mídia local selecionada; o cache do Google Fotos e a soma de tamanhos nunca são aceitos como prova.

## Estado e segurança

A versão 4 do banco adiciona recibos de publicação e diário de operações. O manifesto de staging é reconstituído a partir dos hashes do catálogo, do contêiner verificado e do identificador de composição. O Android conserva itens em IS_PENDING até a checagem dos bytes. O nome da cópia depende do digest; depois de interrupção, o app procura o mesmo item e verifica bytes e propriedade antes de continuar.

A comparação vincula URI com volume, tamanho, hash, geração, caminho e atributos. O digest da ação vincula a proposta e o escopo de confirmação. O caminho de retirada reabre o ZIP, ambos os objetos e os registros imediatamente antes do pedido. Usa [MediaStore](https://developer.android.com/reference/android/provider/MediaStore) com consentimento, sem chamada de exclusão permanente. O usuário atesta reprodução, campos protegidos e que o redundante não está sincronizado. Para nuvem, o app orienta revisão nativa; o diário não promove E1, leitura do SDK ou atestação a confirmação de servidor. O diagnóstico do SDK apenas lê o perfil conhecido e não desbloqueia operações.

Dois trabalhadores recompõem pares elegíveis. Uma conclusão libera a próxima vaga sem aguardar a outra. Falha ou pausa preserva a cópia já finalizada. Lotes usam reserva de disco, Wi-Fi não tarifada e os gates existentes de bateria e temperatura. Sem jobs de escrita em SDK, reinício de serviços, retries remotos ou varredura geral de fotos.

## Validação física pendente

Em Galaxy autorizado com conta sintética, conferir se o JPEG é reconhecido como animado na Galeria e no Google Fotos, se reproduz áudio, se mantém a identidade após o upload e se os atributos permanecem quando uma cópia é movida ou enviada à lixeira. Verificar associação por item, álbuns homônimos e recuperação de um item da nuvem. O APK de teste e os testes AOSP não comprovam compatibilidade Samsung.
