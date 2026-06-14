package com.intifix.modules.technicians.dto.request;

import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarDisponibilidadRequest {

    @NotNull(message = "La disponibilidad es obligatoria")
    private DisponibilidadTecnico disponibilidad;
}
