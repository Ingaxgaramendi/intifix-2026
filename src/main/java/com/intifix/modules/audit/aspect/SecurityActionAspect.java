package com.intifix.modules.audit.aspect;

import com.intifix.modules.audit.config.AuditRequestContext;
import com.intifix.modules.audit.entity.SecurityEventDocument;
import com.intifix.modules.audit.service.SecurityEventService;
import com.intifix.shared.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Registra en {@code security_events} la ejecución de métodos marcados con
 * {@link TrackSecurityAction}. Se enfoca en los fallos (intentos sospechosos:
 * token inválido, IDOR, etc.), que es lo relevante para seguridad.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityActionAspect {

    private final SecurityEventService securityEventService;

    @AfterThrowing(pointcut = "@annotation(trackSecurityAction)", throwing = "ex")
    public void alFallar(TrackSecurityAction trackSecurityAction, Throwable ex) {
        SecurityEventDocument evento = SecurityEventDocument.builder()
                .eventId(UUID.randomUUID())
                .userId(currentUserIdOrNull())
                .email(currentEmailOrNull())
                .reason(trackSecurityAction.value())
                .ipAddress(AuditRequestContext.clientIp())
                .build();
        securityEventService.registrar(evento);
    }

    private UUID currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser usuario) {
            return usuario.getId();
        }
        return null;
    }

    private String currentEmailOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser usuario) {
            return usuario.getCorreo();
        }
        return null;
    }
}
