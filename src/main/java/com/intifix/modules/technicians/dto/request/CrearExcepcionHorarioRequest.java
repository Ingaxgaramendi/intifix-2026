package com.intifix.modules.technicians.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearExcepcionHorarioRequest {

    @NotNull(message = "El idUsuarioTecnico es obligatorio")
    private UUID idUsuarioTecnico;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private ZonedDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private ZonedDateTime fechaFin;

    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    private boolean isFechaFinValid() {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        return fechaFin.isAfter(fechaInicio);
    }

    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String motivo;
}
