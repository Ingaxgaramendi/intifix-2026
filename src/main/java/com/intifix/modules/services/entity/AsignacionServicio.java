package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.EstadoServicio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing the assignment of a service to a technician.
 * 
 * This entity is owned by the services module and stores assignment data.
 * It references services and technicians by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Only one assignment can exist per service at any given time.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Entity
@Table(name = "asignaciones_servicio", indexes = {
    @Index(name = "idx_asignaciones_servicio", columnList = "id_servicio", unique = true),
    @Index(name = "idx_asignaciones_tecnico", columnList = "id_usuario_tecnico"),
    @Index(name = "idx_asignaciones_cotizacion", columnList = "id_cotizacion"),
    @Index(name = "idx_asignaciones_fecha_asignacion", columnList = "fecha_asignacion")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionServicio {

    @Id
    @Column(name = "id_asignacion", nullable = false, updatable = false)
    private UUID idAsignacion;

    @Column(name = "id_servicio", nullable = false, unique = true)
    private UUID idServicio;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "id_cotizacion", nullable = false, unique = true)
    private UUID idCotizacion;

    @Column(name = "fecha_asignacion", nullable = false)
    @Builder.Default
    private ZonedDateTime fechaAsignacion = ZonedDateTime.now();

    @Column(name = "fecha_inicio_estimada")
    private ZonedDateTime fechaInicioEstimada;

    @Column(name = "fecha_inicio_real")
    private ZonedDateTime fechaInicioReal;

    @Column(name = "fecha_fin_estimada")
    private ZonedDateTime fechaFinEstimada;

    @Column(name = "fecha_fin_real")
    private ZonedDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_servicio", nullable = false)
    @Builder.Default
    private EstadoServicio estadoServicio = EstadoServicio.ASIGNADO;

    @Column(name = "notas_tecnico", columnDefinition = "TEXT")
    private String notasTecnico;

    @Column(name = "notas_cliente", columnDefinition = "TEXT")
    private String notasCliente;

    @Column(name = "coordenada_encuentro_lat")
    private Double coordenadaEncuentroLat;

    @Column(name = "coordenada_encuentro_lng")
    private Double coordenadaEncuentroLng;

    @Column(name = "direccion_encuentro", columnDefinition = "TEXT")
    private String direccionEncuentro;

    @PrePersist
    protected void onCreate() {
        if (fechaAsignacion == null) {
            fechaAsignacion = ZonedDateTime.now();
        }
    }
}
