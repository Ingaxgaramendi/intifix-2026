package com.intifix.modules.technicians.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReputacionResponse {

    private UUID idUsuarioTecnico;
    private BigDecimal promedioCalificacion;
    private Integer totalResenas;
    private Integer totalServicios;
    private ZonedDateTime actualizadoEn;
}
