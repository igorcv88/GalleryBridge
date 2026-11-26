# GalleryBridge MediaSync 0.1.0 experimental

Este diretório preserva o módulo separado em Smali, recuperado do APK experimental assinado. A árvore Java usada para o build original ficou na área de trabalho apagada. A montagem abaixo produz um novo arquivo sem assinatura para auditoria. O APK assinado da rodada anterior tem SHA-256 `9ac9898587503eafba78479c34689d7c1366bb9958228609a19967202b7e3d10`. Nunca use a chave de teste do GalleryBridge como chave de produção.

```sh
java -jar apktool_2.12.1.jar b mediasync-experimental -o GalleryBridge-MediaSync-audit-unsigned.apk
```

O módulo verifica processo, versão do agente e conteúdo do manifesto e dos DEX antes de instalar os hooks. A base analisada é `com.samsung.android.agent.storage`, versão `1.6.09.34`, código `160900034`. Os arquivos compilados da Samsung não estão incluídos. O perfil verifica APIs e assinaturas de métodos por reflexão, mas não tem um certificado Samsung aferido na amostra do handoff. A configuração inicial é desativada. O APK tem `minSdk 33` e usa o mecanismo de preferências compartilhadas do LSPosed.

Os pontos observados abrangem `ThermalSyncPolicy.isBlocked`, `DeviceConditionRepositoryImpl.isBatteryLow`, caminhos de cancelamento térmico no SDK, erros RPC e `JobScheduler`. O controle de bateria depende de alimentação, condição e carga reais. O controle térmico é específico ao agente e condicionado ao status e ao headroom reais. Um estado severo desativa a intervenção. O módulo não altera o HAL, limiares globais ou bancos privados.

Ainda é necessária comparação A/B em um Galaxy compatível, com autorização específica e mídia sintética. O teste em AOSP não estabelece compatibilidade em One UI. Consulte o [protocolo](../docs/reconciliation-0.3.1.md).
