package com.intifix.modules.technicians.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearHorarioRequest {

    @NotNull(message = "El idUsuarioTecnico es obligatorio")
    private UUID idUsuarioTecnico;

    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 0, message = "El día de la semana debe estar entre 0 (domingo) y 6 (sábado)")
    @Max(value = 6, message = "El día de la semana debe estar entre 0 (domingo) y 6 (sábado)")
    private Integer diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private Boolean activo;
}
