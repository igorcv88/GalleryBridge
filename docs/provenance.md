# Auditoria de origem

O código desta entrega vem da árvore 0.3.1 do pacote GalleryBridge-0.3.1-entrega.zip. O pacote inclui o bundle com o histórico de desenvolvimento. O GalleryBridge usa um repositório próprio, igorcv88/GalleryBridge.

| Referência | Tratamento |
|---|---|
| takeout_indexar, takeout_auditar_hash e takeout_conferir_cobertura | Ordem, proveniência e verificação binária. Cobertura total por manifesto segue manual |
| takeout_importar_piloto022, takeout_importar_lotes_v3, takeout_migracao_v8 | Reserva, idempotência e verificação. Mutações privadas e exceções fixas não adotadas |
| takeout_migracao_datas_v13, takeout_metadados_datas_v1, takeout_confianca_datas_v2, takeout_conflitos_datas_v11 e takeout_validacao_datas_v3 | Fontes distintas, conflito verificável, preservação de EXIF sem offset |
| takeout_livephoto_datas_v15 | Identidade por tag interna. Nome-base não promove pares |
| takeout_favoritos e takeout_favoritos_aplicar | O comando legado a.apply or a.check chegava a target_one e podia escrever no provider privado. O novo fluxo separa inspeção, consentimento público e verificação local |
| takeout_comparar_local, takeout_auditar_locais_v1 e takeout_auditoria_pendencias_v1 | Match por bytes, ausência não conclusiva, pendências históricas em tabela própria |
| gallery_ambiente_local, gallery_rede_ab, gallery_sync_monitor e MediaSync v4 | A causa da pausa permanece desconhecida. Métricas do UID do helper ficam distintas do tráfego Samsung |

Os scripts foram inspecionados como referência. Este projeto contém código novo e fixtures sintéticas.
