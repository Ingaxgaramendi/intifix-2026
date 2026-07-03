package com.intifix.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Gestiona los índices de MongoDB manualmente (auto-index-creation=false).
 * Permite hacer DROP + recreación cuando cambian opciones como sparse/unique.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MongoIndexMigrationRunner implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        migrateConversacionesIndexes();
    }

    private void migrateConversacionesIndexes() {
        final String col = "conversaciones";

        // Si el índice uk_conversacion_servicio existe sin sparse=true, dropparlo.
        List<IndexInfo> existing = mongoTemplate.indexOps(col).getIndexInfo();
        boolean needsDrop = existing.stream()
            .anyMatch(idx -> "uk_conversacion_servicio".equals(idx.getName()) && !idx.isSparse());

        if (needsDrop) {
            log.info("[Mongo migration] Dropping non-sparse index 'uk_conversacion_servicio' on '{}'", col);
            mongoTemplate.indexOps(col).dropIndex("uk_conversacion_servicio");
        }

        // Crear el índice correcto: único + sparse (idempotente, no-op si ya existe igual)
        mongoTemplate.indexOps(col).ensureIndex(
            new Index("idServicio", Sort.Direction.ASC)
                .named("uk_conversacion_servicio")
                .unique()
                .sparse()
        );

        // Índices compuestos del inbox (idempotentes)
        mongoTemplate.indexOps(col).ensureIndex(
            new Index()
                .on("idCliente", Sort.Direction.ASC)
                .on("actualizadoEn", Sort.Direction.DESC)
                .named("idx_inbox_cliente")
        );
        mongoTemplate.indexOps(col).ensureIndex(
            new Index()
                .on("idTecnico", Sort.Direction.ASC)
                .on("actualizadoEn", Sort.Direction.DESC)
                .named("idx_inbox_tecnico")
        );

        log.info("[Mongo migration] Indexes on '{}' are up to date", col);
    }
}
