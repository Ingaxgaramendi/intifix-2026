package com.intifix.modules.audit.dto.request;

import com.intifix.modules.audit.entity.SecurityReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.util.UUID;

/**
 * Filtros opcionales para listar eventos de seguridad.
 */
public record SecurityEventFilterRequest(

        @Schema(description = "Filtrar por motivo", example = "LOGIN_FAILED")
        SecurityReason reason,

        @Email(message = "El email debe tener un formato válido")
        @Schema(description = "Filtrar por email", example = "user@intifix.com")
        String email,

        @Schema(description = "Filtrar por usuario")
        UUID userId
) {}
