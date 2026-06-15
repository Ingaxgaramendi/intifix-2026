package com.intifix.modules.audit.aspect;

import com.intifix.modules.audit.config.AuditRequestContext;
import com.intifix.modules.audit.entity.AuditEventDocument;
import com.intifix.modules.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Audita automáticamente los métodos anotados con {@link Auditable}: tras una
 * ejecución exitosa persiste un evento de negocio en {@code audit_events}.
 * Cubre casos que no emiten evento de dominio propio (p. ej. cotización aceptada).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditableAspect {

    private final AuditEventService auditEventService;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void auditar(JoinPoint joinPoint, Auditable auditable, Object result) {
        String eventType = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        AuditEventDocument evento = AuditEventDocument.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .module(auditable.module())
                .action(auditable.action())
                .resourceType(auditable.resourceType().isBlank() ? null : auditable.resourceType())
                .userId(AuditRequestContext.currentUserIdOrNull())
                .newValue(result)
                .ipAddress(AuditRequestContext.clientIp())
                .userAgent(AuditRequestContext.userAgent())
                .build();
        auditEventService.registrar(evento);
    }
}
