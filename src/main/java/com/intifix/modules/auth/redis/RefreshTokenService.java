package com.intifix.modules.auth.redis;

import java.util.Optional;
import java.util.UUID;

/**
 * Almacén de refresh tokens. Redis es la única fuente de verdad: un refresh
 * token que no exista aquí está revocado, aunque su firma sea válida.
 */
public interface RefreshTokenService {

    /** Registra un refresh token activo para el usuario, con TTL en segundos. */
    void save(String tokenId, UUID userId, long ttlSeconds);

    /** Devuelve el userId dueño del token si sigue activo; vacío si expiró o fue revocado. */
    Optional<UUID> find(String tokenId);

    /** Elimina (invalida) un refresh token puntual. */
    void delete(String tokenId);

    /** Revoca TODOS los refresh tokens del usuario (logout global, ban, cambio de contraseña). */
    void revoke(UUID userId);
}
