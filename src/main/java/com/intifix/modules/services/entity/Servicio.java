package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a service request in the INTIFIX platform.
 * 
 * This entity is owned by the services module and stores all service-related data.
 * It references clients and locations by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Implements Persistable<UUID> to avoid unnecessary SELECT before INSERT.
 * The isNew() method returns true when the ID is null, allowing Hibernate
 * to perform a direct INSERT without checking for existence first.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Entity
@Table(name = "servicios", indexes = {
    @Index(name = "idx_servicios_cliente", columnList = "id_cliente"),
    @Index(name = "idx_servicios_ubicacion", columnList = "id_ubicacion"),
    @Index(name = "idx_servicios_especialidad", columnList = "id_especialidad"),
    @Index(name = "idx_servicios_estado", columnList = "estado"),
    @Index(name = "idx_servicios_fecha_creacion", columnList = "fecha_creacion")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio implements Persistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_servicio", nullable = false, updatable = false)
    private UUID idServicio;

    @Column(name = "id_cliente", nullable = false)
    private UUID idCliente;

    // Opcional: solo se registra para servicios EN_CASA_CLIENTE. Para
    // EN_TALLER_TECNICO el cliente acude al taller, así que no hay ubicación.
    @Column(name = "id_ubicacion")
    private UUID idUbicacion;

    // Specialty the service belongs to (computers, plumbing, carpentry, etc.) so
    // technicians immediately know which trade a request needs. Nullable so legacy
    // rows created before this column stay valid.
    @Column(name = "id_especialidad")
    private UUID idEspecialidad;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // Fotos del servicio (1..5 URLs en Cloudinary). Se almacenan como text[] de
    // Postgres; el límite se valida en el request. Nullable para filas antiguas.
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "fotos", columnDefinition = "text[]")
    private List<String> fotos;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "modalidad", nullable = false)
    private ModalidadServicio modalidad;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_solicitud", nullable = false)
    @Builder.Default
    private TipoSolicitud tipoSolicitud = TipoSolicitud.PUBLICA;

    // Solo presente cuando tipoSolicitud == DIRECTA; referencia al técnico elegido.
    @Column(name = "id_tecnico_directo")
    private UUID idTecnicoDirecto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoServicio estado = EstadoServicio.PENDIENTE;

    @Column(name = "presupuesto_maximo", precision = 12, scale = 2)
    private BigDecimal presupuestoMaximo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_fecha", nullable = false)
    @Builder.Default
    private TipoFecha tipoFecha = TipoFecha.EXACTA;

    /** Set for EXACTA mode. Null for URGENTE and RANGO. */
    @Column(name = "fecha_programada")
    private ZonedDateTime fechaProgramada;

    /** Start of range for RANGO mode. */
    @Column(name = "fecha_inicio_rango")
    private ZonedDateTime fechaInicioRango;

    /** End of range for RANGO mode (max 5 days after fechaInicioRango). */
    @Column(name = "fecha_fin_rango")
    private ZonedDateTime fechaFinRango;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private ZonedDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "fecha_finalizacion")
    private ZonedDateTime fechaFinalizacion;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return idServicio;
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
