package com.intifix.shared.security;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

/**
 * Principal autenticado de la aplicación. Reemplaza el uso de String (correo)
 * como principal: el identificador estable del usuario es su UUID, lo que
 * habilita validaciones de ownership ({@code #id == principal.id}) en
 * {@code @PreAuthorize} y previene IDOR.
 *
 * <p>Vive en shared porque es el contrato transversal que todos los módulos
 * consumen; no contiene lógica de autenticación (esa vive en modules/auth).</p>
 */
@Getter
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "roles"})
public class AuthenticatedUser implements Principal {

    private final UUID id;
    private final String correo;
    private final Set<String> roles;

    /**
     * Nombre canónico del principal: el UUID del usuario (no el correo).
     * Usado por Spring (STOMP user destinations, Authentication#getName).
     */
    @Override
    public String getName() {
        return id.toString();
    }

    public boolean tieneRol(String rol) {
        return roles != null && roles.contains(rol);
    }
}
