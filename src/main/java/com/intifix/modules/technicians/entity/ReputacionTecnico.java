package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "reputacion_tecnico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReputacionTecnico {

    @Id
    @Column(name = "id_usuario_tecnico", nullable = false, updatable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "promedio_calificacion", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal promedioCalificacion = BigDecimal.ZERO;

    @Column(name = "total_resenas", nullable = false)
    @Builder.Default
    private Integer totalResenas = 0;

    @Column(name = "total_servicios", nullable = false)
    @Builder.Default
    private Integer totalServicios = 0;

    @Column(name = "actualizado_en", nullable = false)
    @Builder.Default
    private ZonedDateTime actualizadoEn = ZonedDateTime.now(ZoneId.systemDefault());

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = ZonedDateTime.now(ZoneId.systemDefault());
    }
}
