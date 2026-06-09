package com.intifix.modules.technicians.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoResponse {
    private UUID usuarioId;
    private String nombresCompletos;
    private String dniRuc;
    private Integer experienciaAnios;
    private String estadoAprobacion;
    private String disponibilidad;
    private BigDecimal tarifaBase;
    private Double reputacionPromedio;
    private Integer totalResenas;
    private Integer totalServicios;
    private Set < String > especialidades;
    private Set < HorarioDTO > horarios;
}
