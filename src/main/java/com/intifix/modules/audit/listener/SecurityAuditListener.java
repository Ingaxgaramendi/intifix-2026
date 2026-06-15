package com.intifix.modules.audit.listener;

import com.intifix.modules.audit.config.AuditRequestContext;
import com.intifix.modules.audit.entity.SecurityEventDocument;
import com.intifix.modules.audit.entity.SecurityReason;
import com.intifix.modules.audit.service.SecurityEventService;
import com.intifix.shared.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Registra eventos de seguridad escuchando los que ya publica Spring Security
 * (login OK/fallido y acceso denegado), sin tocar el módulo auth.
 *
 * <p>Los demás motivos ({@code JWT_INVALID}, {@code JWT_EXPIRED},
 * {@code REFRESH_TOKEN_INVALID}, {@code IDOR_ATTEMPT}, {@code BRUTE_FORCE}) se
 * registran desde el flujo de auth/aspectos publicando un
 * {@link SecurityEventDocument} hacia {@link SecurityEventService}.</p>
 */
@Component
@RequiredArgsConstructor
public class SecurityAuditListener {

    private final SecurityEventService securityEventService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        registrar(SecurityReason.LOGIN_SUCCESS, extraerUserId(auth), auth.getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        Authentication auth = event.getAuthentication();
        registrar(SecurityReason.LOGIN_FAILED, null, auth == null ? null : auth.getName());
    }

    @EventListener
    public void onAccessDenied(AuthorizationDeniedEvent<?> event) {
        Authentication auth = event.getAuthentication() == null ? null : event.getAuthentication().get();
        registrar(SecurityReason.ACCESS_DENIED, extraerUserId(auth), auth == null ? null : auth.getName());
    }

    private void registrar(SecurityReason reason, UUID userId, String email) {
        SecurityEventDocument evento = SecurityEventDocument.builder()
                .eventId(UUID.randomUUID())
                .userId(userId)
                .email(email)
                .reason(reason)
                .ipAddress(AuditRequestContext.clientIp())
                .build();
        securityEventService.registrar(evento);
    }

    private UUID extraerUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser usuario) {
            return usuario.getId();
        }
        return null;
    }
}
