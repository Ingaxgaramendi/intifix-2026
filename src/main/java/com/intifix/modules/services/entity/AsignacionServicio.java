package com.intifix.modules.services.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "asignaciones_servicio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionServicio {

    @Id
    @Column(name = "id_asignacion")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false, unique = true)
    private Servicio servicio;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "id_cotizacion", nullable = false, unique = true)
    private UUID idCotizacion; // Solo guardamos el UUID para desacoplar el módulo de cotizaciones

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime fechaAsignacion = OffsetDateTime.now();
}
