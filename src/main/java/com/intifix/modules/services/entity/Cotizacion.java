package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.EstadoCotizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing a quotation for a service in the INTIFIX platform.
 * 
 * This entity is owned by the services module and stores quotation data.
 * It references services and technicians by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Implements Persistable<UUID> to avoid unnecessary SELECT before INSERT.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Entity
@Table(name = "cotizaciones", indexes = {
    @Index(name = "idx_cotizaciones_servicio", columnList = "id_servicio"),
    @Index(name = "idx_cotizaciones_tecnico", columnList = "id_usuario_tecnico"),
    @Index(name = "idx_cotizaciones_estado", columnList = "estado"),
    @Index(name = "idx_cotizaciones_fecha_envio", columnList = "fecha_envio")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion implements Persistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cotizacion", nullable = false, updatable = false)
    private UUID idCotizacion;

    @Column(name = "id_servicio", nullable = false)
    private UUID idServicio;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    // VARCHAR(100) en BD: texto libre tipo "2 horas", "3 días"
    @Column(name = "tiempo_estimado", nullable = false, length = 100)
    private String tiempoEstimado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private ZonedDateTime fechaEnvio;

    @Column(name = "fecha_respuesta")
    private ZonedDateTime fechaRespuesta;

    @Column(name = "fecha_expiracion")
    private ZonedDateTime fechaExpiracion;

    /** Fecha y hora propuesta por el técnico. Requerida para modos URGENTE y RANGO. */
    @Column(name = "fecha_propuesta")
    private ZonedDateTime fechaPropuesta;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return idCotizacion;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    public void markNotNew() {
        this.isNew = false;
    }

    @PrePersist
    protected void onCreate() {
        // Set expiration to 48 hours from now (business logic, cannot be delegated to DB)
        if (fechaExpiracion == null) {
            fechaExpiracion = ZonedDateTime.now(ZoneId.systemDefault()).plusHours(48);
        }
    }
}
