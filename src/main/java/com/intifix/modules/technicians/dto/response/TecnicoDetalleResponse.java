package com.intifix.modules.technicians.dto.response;

import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoDetalleResponse {

    private UUID idUsuario;
    private String nombresCompletos;
    private String dniRuc;
    private Integer experienciaAnios;
    private EstadoAprobacionTecnico estadoAprobacion;
    private DisponibilidadTecnico disponibilidad;
    private BigDecimal tarifaBase;
    private UUID idUbicacion;
    private String dniFrontalUrl;
    private String dniTraseroUrl;
    private String antecedentePenalUrl;
    private String certificadoTecnicoUrl;
    private ZonedDateTime creadoEn;
    private List<HorarioResponse> horarios;
    private List<EspecialidadResponse> especialidades;
    private ReputacionResponse reputacion;
}
