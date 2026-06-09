package com.intifix.modules.technicians.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDTO {
    private Integer diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    @Builder.Default
    private Boolean activo = true;
}
