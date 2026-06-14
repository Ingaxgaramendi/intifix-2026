package com.intifix.modules.technicians.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarHorarioRequest {

    @Min(value = 0, message = "El día de la semana debe estar entre 0 (domingo) y 6 (sábado)")
    @Max(value = 6, message = "El día de la semana debe estar entre 0 (domingo) y 6 (sábado)")
    private Integer diaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @AssertTrue(message = "La hora de fin debe ser posterior a la hora de inicio")
    private boolean isHoraFinValid() {
        if (horaInicio == null || horaFin == null) {
            return true;
        }
        return horaFin.isAfter(horaInicio);
    }

    private Boolean activo;
}
