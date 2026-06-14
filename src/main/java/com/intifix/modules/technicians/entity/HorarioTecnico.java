package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "horarios_tecnico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioTecnico {

    @Id
    @Column(name = "id_horario")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idHorario;

    @Column(name = "id_usuario_tecnico", nullable = false, updatable = false)
    private UUID idUsuarioTecnico;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
