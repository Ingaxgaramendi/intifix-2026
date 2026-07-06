package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "excepciones_horario_tecnico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcepcionHorarioTecnico {

    @Id
    @Column(name = "id_excepcion")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idExcepcion;

    @Column(name = "id_usuario_tecnico", nullable = false, updatable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "fecha_inicio", nullable = false)
    private ZonedDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private ZonedDateTime fechaFin;

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    @Builder.Default
    private ZonedDateTime creadoEn = ZonedDateTime.now(ZoneId.systemDefault());

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = ZonedDateTime.now(ZoneId.systemDefault());
        }
    }
}
