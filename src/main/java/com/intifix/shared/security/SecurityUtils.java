package com.intifix.shared.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Acceso tipado al usuario autenticado. Trabaja exclusivamente con
 * {@link AuthenticatedUser} como principal; si el contexto no contiene uno,
 * falla con una excepción de Spring Security (traducida a 401), nunca con
 * una RuntimeException genérica.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID obtenerIdUsuarioAutenticado() {
        return obtenerUsuarioAutenticado().getId();
    }

    /**
     * Alias de {@link #obtenerIdUsuarioAutenticado()} usado por los módulos
     * chat, payments, notifications, ai y geo.
     */
    public static UUID currentUserId() {
        return obtenerIdUsuarioAutenticado();
    }

    public static String obtenerCorreoAutenticado() {
        return obtenerUsuarioAutenticado().getCorreo();
    }

    public static boolean tieneRol(String rol) {
        return obtenerUsuarioAutenticado().tieneRol(rol);
    }

    public static boolean esPropietario(UUID idUsuario) {
        return idUsuario != null && idUsuario.equals(obtenerUsuarioAutenticado().getId());
    }

    public static AuthenticatedUser obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser usuario)) {
            throw new AuthenticationCredentialsNotFoundException(
                "No hay un usuario autenticado en el contexto de seguridad.");
        }
        return usuario;
    }
}
