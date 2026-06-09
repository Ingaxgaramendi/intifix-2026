package com.intifix.modules.services.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "calificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @Column(name = "id_calificacion")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false, unique = true)
    private Servicio servicio;

    @Column(name = "id_cliente", nullable = false)
    private UUID idCliente;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID idUsuarioTecnico;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime fecha = OffsetDateTime.now();
}
