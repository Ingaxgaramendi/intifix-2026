package com.intifix.modules.technicians.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class TechUpdateRequest {
    private String nombresCompletos;
    private Integer experienciaAnios;
    private BigDecimal tarifaBase;
    private Set < UUID > especialidadesIds;
    private Set < HorarioDTO > horarios;
}
