package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica al cancelarse un servicio.
 */
public record ServiceCancelledEvent(
        UUID serviceId,
        UUID cancelledBy,
        String motivo
) {}
