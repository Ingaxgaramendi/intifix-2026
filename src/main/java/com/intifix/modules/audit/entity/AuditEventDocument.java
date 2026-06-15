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
 * Evento de negocio en la colección {@code audit_events}: traza inmutable de
 * "quién hizo qué sobre qué recurso y cuándo".
 *
 * <p>Guarda el snapshot anterior y nuevo del recurso ({@code oldValue}/
 * {@code newValue}) como objetos libres para soportar diffs sin acoplarse al
 * esquema de cada módulo. La escritura es append-only: nunca se actualiza.</p>
 */
@Document(collection = "audit_events")
@CompoundIndexes({
    @CompoundIndex(name = "idx_audit_module_ts", def = "{'module': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_audit_user_ts", def = "{'userId': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventDocument {

    @Id
    private UUID eventId;

    @Indexed(name = "idx_audit_event_type")
    private String eventType;

    private AuditModule module;

    private UUID userId;

    private UUID resourceId;

    private String resourceType;

    private AuditAction action;

    private String ipAddress;

    private String userAgent;

    private Object oldValue;

    private Object newValue;

    @CreatedDate
    private Instant timestamp;
}
