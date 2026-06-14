package com.intifix.modules.users.gateway;

import java.util.UUID;

/**
 * Puerto de salida hacia el contexto de identidad (módulo auth / futuro
 * intifix-auth-service).
 *
 * <p>El módulo users NO depende de clases de otros módulos: toda integración
 * pasa por esta interfaz. Al extraer el módulo como microservicio, basta con
 * sustituir el adaptador local por un Feign/REST client o una proyección
 * alimentada por eventos, sin tocar la lógica de negocio.</p>
 */
public interface UserGateway {

    /**
     * Verifica que el usuario exista y no esté eliminado lógicamente.
     */
    boolean existeUsuario(UUID idUsuario);
}
