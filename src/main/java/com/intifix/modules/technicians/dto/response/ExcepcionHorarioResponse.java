package com.intifix.modules.technicians.dto.response;

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
public class ExcepcionHorarioResponse {

    private UUID idExcepcion;
    private UUID idUsuarioTecnico;
    private ZonedDateTime fechaInicio;
    private ZonedDateTime fechaFin;
    private String motivo;
    private ZonedDateTime creadoEn;
}
