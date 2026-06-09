package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
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
    @Column(name = "id_usuario_tecnico")
    private UUID idUsuarioTecnico;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Vincula la PK de esta tabla con la PK de PerfilTecnicoOperativo de forma soplete
    @JoinColumn(name = "id_usuario_tecnico")
    private PerfilTecnicoOperativo tecnico;

    @Column(name = "promedio", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private Double promedio = 0.00;

    @Column(name = "total_resenas", nullable = false)
    @Builder.Default
    private Integer totalResenas = 0;

    @Column(name = "total_servicios", nullable = false)
    @Builder.Default
    private Integer totalServicios = 0;

    @Column(name = "actualizado_en", nullable = false)
    @Builder.Default
    private OffsetDateTime actualizadoEn = OffsetDateTime.now();
}
