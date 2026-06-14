package com.intifix.modules.services.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing a rating for a completed service.
 * 
 * This entity is owned by the services module and stores rating data.
 * It references services and technicians by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Only one rating is allowed per service, and only for finished services.
 * 
 * Implements Persistable<UUID> to avoid unnecessary SELECT before INSERT.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Entity
@Table(name = "calificaciones", indexes = {
    @Index(name = "idx_calificaciones_servicio", columnList = "id_servicio", unique = true),
    @Index(name = "idx_calificaciones_tecnico", columnList = "id_usuario_tecnico"),
    @Index(name = "idx_calificaciones_cliente", columnList = "id_cliente"),
    @Index(name = "idx_calificaciones_fecha", columnList = "fecha")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion implements Persistable<UUID> {

    @Id
    @Column(name = "id_calificacion", nullable = false, updatable = false)
    private UUID idCalificacion;

    @Column(name = "id_servicio", nullable = false, unique = true)
    private UUID idServicio;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "id_cliente", nullable = false)
    private UUID idCliente;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "puntualidad")
    private Integer puntualidad;

    @Column(name = "profesionalismo")
    private Integer profesionalismo;

    @Column(name = "calidad_trabajo")
    private Integer calidadTrabajo;

    @Column(name = "comunicacion")
    private Integer comunicacion;

    @Column(name = "recomendaria")
    private Boolean recomendaria;

    // La columna en BD se llama "fecha"
    @Column(name = "fecha", nullable = false)
    private ZonedDateTime fechaCalificacion;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "aspectos_positivos", columnDefinition = "TEXT[]")
    private String[] aspectosPositivos;

    @Column(name = "aspectos_mejorar", columnDefinition = "TEXT[]")
    private String[] aspectosMejorar;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return idCalificacion;
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
