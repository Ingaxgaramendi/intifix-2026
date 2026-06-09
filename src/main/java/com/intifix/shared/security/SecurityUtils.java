package com.intifix.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtils {

    public static UUID obtenerIdUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad.");
        }
        // Asumiendo que guardas el UUID del usuario como principal (o string) en el token
        return UUID.fromString(authentication.getName());
    }
}
