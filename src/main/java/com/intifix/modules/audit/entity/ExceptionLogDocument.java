package com.intifix.modules.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Error no controlado capturado por el aspecto global en {@code exception_logs}.
 * Guarda la traza completa para diagnóstico post-mortem, asociada al usuario y
 * módulo donde se originó.
 */
@Document(collection = "exception_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_exc_module_ts", def = "{'module': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionLogDocument {

    @Id
    private UUID id;

    @Indexed(name = "idx_exc_class")
    private String exceptionClass;

    private String message;

    private String stackTrace;

    private String module;

    private UUID userId;

    @CreatedDate
    private Instant timestamp;
}
