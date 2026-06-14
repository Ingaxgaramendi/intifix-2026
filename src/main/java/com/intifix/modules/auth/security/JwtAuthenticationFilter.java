package com.intifix.modules.auth.security;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.exception.InvalidTokenException;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import com.intifix.shared.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Único filtro JWT de la aplicación.
 *
 * <p>Construye un {@link AuthenticatedUser} (UUID + correo + roles) como
 * principal — nunca un String — habilitando ownership checks en
 * {@code @PreAuthorize} y {@code SecurityUtils}. Rechaza refresh tokens
 * como credencial HTTP y verifica contra BD que la cuenta siga activa.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwtFromRequest(request);
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtTokenProvider.extraerClaims(jwt);

            if (!JwtTokenProvider.TOKEN_TYPE_ACCESS.equals(jwtTokenProvider.obtenerTipo(claims))) {
                log.debug("Se intentó autenticar con un token que no es de tipo access");
                filterChain.doFilter(request, response);
                return;
            }

            UUID userId = jwtTokenProvider.obtenerUserId(claims);

            // El estado de la cuenta se verifica contra BD (proyección ligera,
            // no carga la entidad) para que bans/suspensiones apliquen de inmediato.
            EstadoUsuario estado = usuarioAuthRepository.obtenerEstadoPorId(userId).orElse(null);
            if (estado == null) {
                log.warn("Token válido para un usuario que ya no existe: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }
            if (estado != EstadoUsuario.ACTIVO) {
                log.warn("Acceso denegado por estado de cuenta {} para usuario: {}", estado, userId);
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "La cuenta no se encuentra activa.");
                return;
            }

            List<String> roles = jwtTokenProvider.obtenerRoles(claims);

            AuthenticatedUser principal = AuthenticatedUser.builder()
                .id(userId)
                .correo(jwtTokenProvider.obtenerCorreo(claims))
                .roles(Set.copyOf(roles))
                .build();

            var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (InvalidTokenException e) {
            log.debug("Token inválido: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/register") ||
               path.startsWith("/api/v1/auth/refresh") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/health");
    }
}
