package com.intifix.modules.audit.dto.request;

import com.intifix.modules.audit.entity.AuditModule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Filtros opcionales para listar eventos de negocio. Todos son nullables: si no
 * se envía ninguno se devuelve la traza completa paginada.
 */
public record AuditEventFilterRequest(

        @Schema(description = "Filtrar por módulo de origen", example = "PAYMENTS")
        AuditModule module,

        @Schema(description = "Filtrar por tipo de evento", example = "PaymentCompletedEvent")
        String eventType,

        @Schema(description = "Filtrar por usuario actor")
        UUID userId
) {}
