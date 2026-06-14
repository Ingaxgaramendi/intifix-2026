package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.EstadoServicio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing the history of state changes for a service.
 * 
 * This entity is owned by the services module and stores audit trail data.
 * It automatically records every state change for services, providing
 * a complete audit trail for debugging and accountability.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Entity
@Table(name = "historial_servicio", indexes = {
    @Index(name = "idx_historial_servicio", columnList = "id_servicio"),
    @Index(name = "idx_historial_fecha_cambio", columnList = "fecha_cambio")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialServicio {

    @Id
    @Column(name = "id_historial", nullable = false, updatable = false)
    private UUID idHistorial;

    @Column(name = "id_servicio", nullable = false)
    private UUID idServicio;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_anterior")
    private EstadoServicio estadoAnterior;

    // La columna en BD se llama "estado" (el estado al que transicionó el servicio)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoServicio estadoNuevo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "cambiado_por")
    private UUID cambiadoPor;

    @Column(name = "rol_cambiado_por", length = 50)
    private String rolCambiadoPor;

    @Column(name = "fecha_cambio", nullable = false)
    @Builder.Default
    private ZonedDateTime fechaCambio = ZonedDateTime.now();

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        if (fechaCambio == null) {
            fechaCambio = ZonedDateTime.now();
        }
    }
}
