package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica al crearse un servicio.
 */
public record ServiceCreatedEvent(
        UUID serviceId,
        UUID clientId
) {}
