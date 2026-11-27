# CI, assinatura e publicação

## CI (Android CI)

O workflow `android.yml` dispara em todo push de branch (um run para o HEAD de cada push) e por execução manual. Não dispara novamente por pull_request: isso duplicava testes para o mesmo commit. O grupo de concorrência `gallerybridge-ci-${{ github.ref }}` cancela a execução anterior da mesma branch quando uma nova começa, sem cancelar CIs de branches independentes nem releases. Runs iniciados com a configuração antiga não são cancelados retroativamente. O job host valida o wrapper, usa JDK 21, SDK/Build Tools 36, executa testes JVM de engine e app (debug e release), lint debug/release, Detekt e builds debug, androidTest e release. A matriz AOSP mantém API 30 e 37.0 do workflow anterior, reutilizando os APKs de instrumentação. A assinatura oficial só ocorre depois dos checks, em pushes do repositório e execução manual autorizada. PRs de forks não disparam esta CI automaticamente nem recebem a chave privada; para validar uma contribuição externa, revise-a e importe-a para uma branch da origem antes de disparar CI. PRs da própria origem têm APK oficial no run de push correspondente. Os APKs de instrumentação são exclusivamente de teste, assinados com chave debug. Não instalar como release.

Todos os uploads usam `retention-days: 1` (expiração de 24h gerenciada pelo GitHub; a remoção física e atualização de armazenamento são assíncronas). Os artefatos anteriores à mudança não têm a retenção retroativamente alterada. A publicação estável não usa upload-artifact.

## Secrets

Configure em Settings → Secrets and variables → Actions → New repository secret:

| Secret | Conteúdo |
| --- | --- |
| KEYSTORE_BASE64 | Arquivo JKS/PKCS12 codificado em base64 |
| KEYSTORE_PASSWORD | Senha do keystore |
| KEY_ALIAS | Alias da chave |
| KEY_PASSWORD | Senha da chave |

Mesmo contrato do WaEnhancerXCommunity. Não coloque valores em arquivos ou logs. O APK release sem assinatura é alinhado e assinado com apksigner; a validação compara SHA-256 do certificado extraído do keystore, pacote, versão e assinatura v2/v3. A chave temporária é removida no EXIT e não é armazenada no cache. Não há fallback para debug. Instalações antigas com outra chave não podem receber atualização direta.

## Release

Somente PR fechado com merged=true em main. O evento pull_request_target fechado permite publicar inclusive PRs externos *já revisados e mesclados*. O workflow nunca executa o head do PR: faz checkout do merge_commit_sha e exige que seja ancestral de origin/main antes de rodar código. Mantém os checks host e a mesma matriz de instrumentação no próprio runner, sem artefatos Actions. Assina e publica APK e checksum em Releases. O label `release:prerelease` antes do merge produz uma tag exclusiva pre-prN-SHA, sem alterar latest. O padrão é uma release estável com tag móvel literal `latest` e marcada Latest. O workflow envia os novos assets antes de mover a tag com lease; depois limpa apenas os assets antigos gerados pelo mesmo padrão. Atualização de release/tag/assets não é transação atômica; em falha, inspecione Releases e reexecute o mesmo run. Releases imutáveis ou proteção que impeça atualizar a tag latest são incompatíveis com este modelo. O controle de concorrência serializa publicações e o script impede regressão de latest; o GitHub pode substituir runs pendentes quando vários merges ocorrem simultaneamente. A versão Android continua definida no Gradle; aumente versionCode para versões novas quando necessário.

O workflow só entra em operação após existir em main; o primeiro merge que o introduz pode exigir o próximo merge para iniciar a publicação. Não há dispatch manual de release nem trigger por tag ou push. Nenhuma publicação é feita por este PR de infraestrutura antes de chegar a main. Testes AOSP não certificam sincronização Samsung/Google Fotos.
