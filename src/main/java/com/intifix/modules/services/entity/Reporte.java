package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.EstadoReporte;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing a report filed for a service or user behavior.
 * 
 * This entity is owned by the services module and stores report data.
 * It references services and users by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Reports are used for moderation and dispute resolution.
 * 
 * Implements Persistable<UUID> to avoid unnecessary SELECT before INSERT.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Entity
@Table(name = "reportes", indexes = {
    @Index(name = "idx_reportes_servicio", columnList = "id_servicio"),
    @Index(name = "idx_reportes_reportante", columnList = "id_usuario_reporta"),
    @Index(name = "idx_reportes_reportado", columnList = "id_reportado"),
    @Index(name = "idx_reportes_estado", columnList = "estado"),
    @Index(name = "idx_reportes_fecha_reporte", columnList = "fecha")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reporte implements Persistable<UUID> {

    @Id
    @Column(name = "id_reporte", nullable = false, updatable = false)
    private UUID idReporte;

    // En BD id_servicio es NOT NULL (todo reporte nace de un servicio)
    @Column(name = "id_servicio", nullable = false)
    private UUID idServicio;

    // La columna en BD se llama "id_usuario_reporta"
    @Column(name = "id_usuario_reporta", nullable = false)
    private UUID idReportante;

    @Column(name = "id_reportado")
    private UUID idReportado;

    @Column(name = "tipo_reporte", length = 50)
    private String tipoReporte;

    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "descripcion_detallada", columnDefinition = "TEXT")
    private String descripcionDetallada;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @Column(name = "prioridad", length = 20)
    private String prioridad;

    @Column(name = "resolucion", columnDefinition = "TEXT")
    private String resolucion;

    @Column(name = "accion_tomada", columnDefinition = "TEXT")
    private String accionTomada;

    @Column(name = "resuelto_por")
    private UUID resueltoPor;

    @Column(name = "fecha_resolucion")
    private ZonedDateTime fechaResolucion;

    // La columna en BD se llama "fecha"
    @Column(name = "fecha", nullable = false)
    private ZonedDateTime fechaReporte;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "evidencias_url", columnDefinition = "TEXT[]")
    private String[] evidenciasUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadatos", columnDefinition = "JSONB")
    private String metadatos;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return idReporte;
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
}
