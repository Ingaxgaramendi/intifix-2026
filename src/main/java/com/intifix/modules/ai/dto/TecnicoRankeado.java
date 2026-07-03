package com.intifix.modules.ai.dto;

import com.intifix.modules.technicians.dto.response.TecnicoResponse;

import java.math.BigDecimal;

/**
 * Técnico enriquecido con su reputación, para la lógica de ranking (GOSU).
 */
public record TecnicoRankeado(
        TecnicoResponse tecnico,
        BigDecimal promedioCalificacion,
        Integer totalResenas,
        Integer totalServicios
) {
}
