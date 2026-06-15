package com.intifix.modules.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Log de una request HTTP en {@code api_logs}. Es el flujo de mayor volumen,
 * por eso tiene <strong>TTL de 90 días</strong> ({@code expireAfterSeconds})
 * sobre {@code timestamp}: MongoDB purga automáticamente los documentos viejos.
 */
@Document(collection = "api_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_apilog_path_ts", def = "{'path': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_apilog_user_ts", def = "{'userId': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDocument {

    @Id
    private UUID requestId;

    private String method;

    private String path;

    private int status;

    private long durationMs;

    private UUID userId;

    private String ipAddress;

    // TTL: MongoDB elimina el documento 90 días (7.776.000 s) después de timestamp.
    @Indexed(name = "idx_apilog_ttl", expireAfterSeconds = 7_776_000)
    private Instant timestamp;
}
