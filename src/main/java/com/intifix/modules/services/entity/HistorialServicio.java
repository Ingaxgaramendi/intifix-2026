package com.intifix.modules.services.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_servicio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialServicio {

    @Id
    @Column(name = "id_historial")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_cambio", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime fechaCambio = OffsetDateTime.now();
}
