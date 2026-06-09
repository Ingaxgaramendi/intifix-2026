package com.intifix.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AiDiagnosisRequest(
        @NotNull UUID servicioId,
        @NotBlank @Size(min = 10, max = 4000) String problema
) {
}
