package com.intifix.modules.audit.config;

import com.intifix.modules.audit.entity.ApiLogDocument;
import com.intifix.modules.audit.service.ApiLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Registra cada request HTTP en {@code api_logs} (método, ruta, status, latencia,
 * usuario, IP). Mide la duración alrededor de toda la cadena y lee el
 * {@code SecurityContext} <em>después</em> de procesarla, por lo que se registra
 * con orden posterior a la cadena de Spring Security (ver {@link AuditConfig})
 * para que el usuario ya esté autenticado.
 *
 * <p>La persistencia se delega a un servicio asíncrono: el logging de auditoría
 * no añade latencia perceptible a la respuesta.</p>
 */
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final ApiLogService apiLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long inicio = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            try {
                ApiLogDocument log = ApiLogDocument.builder()
                        .requestId(UUID.randomUUID())
                        .method(request.getMethod())
                        .path(request.getRequestURI())
                        .status(response.getStatus())
                        .durationMs(duracion)
                        .userId(AuditRequestContext.currentUserIdOrNull())
                        .ipAddress(AuditRequestContext.clientIp(request))
                        .timestamp(Instant.now())
                        .build();
                apiLogService.registrar(log);
            } catch (Exception ignored) {
                // El registro de auditoría nunca debe afectar la respuesta al cliente.
            }
        }
    }

    /** No registrar el ruido de infraestructura (health, swagger, websocket handshake). */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/ws");
    }
}
