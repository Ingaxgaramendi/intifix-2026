package com.intifix.modules.audit.dto.response;

import com.intifix.modules.audit.entity.AuditAction;
import com.intifix.modules.audit.entity.AuditModule;

import java.time.Instant;
import java.util.UUID;

/**
 * Vista de lectura de un evento de negocio para los dashboards de administración.
 */
public record AuditEventResponse(
        UUID eventId,
        String eventType,
        AuditModule module,
        UUID userId,
        UUID resourceId,
        String resourceType,
        AuditAction action,
        String ipAddress,
        String userAgent,
        Object oldValue,
        Object newValue,
        Instant timestamp
) {}
