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
 * Evento de seguridad en {@code security_events}: login OK/fallido, JWT
 * inválido/expirado, acceso denegado, fuerza bruta e intentos IDOR.
 *
 * <p>Soporta detección de abuso: los índices por email e IP permiten contar
 * intentos fallidos recientes para alertas de fuerza bruta.</p>
 */
@Document(collection = "security_events")
@CompoundIndexes({
    @CompoundIndex(name = "idx_sec_email_ts", def = "{'email': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_sec_ip_ts", def = "{'ipAddress': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEventDocument {

    @Id
    private UUID eventId;

    private UUID userId;

    private String email;

    private String ipAddress;

    @Indexed(name = "idx_sec_reason")
    private SecurityReason reason;

    private String country;

    @CreatedDate
    private Instant timestamp;
}
