package com.intifix.modules.audit.config;

import com.intifix.shared.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Extrae datos del contexto de la request en curso (IP, User-Agent, usuario
 * autenticado) para enriquecer la auditoría.
 *
 * <p>Debe invocarse en el hilo de la request (listeners síncronos, filtro,
 * aspectos). La persistencia posterior es asíncrona, pero la captura del
 * contexto ocurre aquí, de forma síncrona, donde el contexto está disponible.
 * Todos los métodos son null-safe: fuera de una request devuelven {@code null}.</p>
 */
public final class AuditRequestContext {

    private AuditRequestContext() {
    }

    public static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest();
        }
        return null;
    }

    public static String clientIp() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : clientIp(request);
    }

    /**
     * IP real del cliente respetando el proxy/balanceador (Upstash, Cloud):
     * prioriza el primer valor de {@code X-Forwarded-For}.
     */
    public static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static String userAgent() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : request.getHeader("User-Agent");
    }

    /** UUID del usuario autenticado o {@code null} si la request es anónima. */
    public static UUID currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser usuario) {
            return usuario.getId();
        }
        return null;
    }
}
