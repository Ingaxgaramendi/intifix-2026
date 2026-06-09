package com.intifix.modules.ai.dto;

import com.intifix.modules.ai.entity.CategoriaDiagnostico;
import com.intifix.modules.technicians.entity.EspecialidadTecnico;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AiDiagnosisResponse(
        UUID diagnosticoId,
        CategoriaDiagnostico categoria,
        BigDecimal confianza,
        String resumen,
        List<SpecialtySuggestion> sugerencias
) {
    public record SpecialtySuggestion(EspecialidadTecnico especialidad, BigDecimal score) {
    }
}
