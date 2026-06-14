package com.intifix.modules.users.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador local del {@link UserGateway} para el despliegue monolítico
 * modular: consulta la tabla {@code usuarios} por SQL directo, sin importar
 * entidades ni repositorios del módulo auth.
 *
 * <p>En la separación a microservicios este adaptador se reemplaza por un
 * cliente HTTP (Feign/RestClient) contra intifix-auth-service o por una
 * réplica local alimentada por eventos {@code UsuarioCreado}.</p>
 */
@Component
@RequiredArgsConstructor
class JdbcUserGatewayAdapter implements UserGateway {

    private static final String EXISTS_SQL =
        "SELECT EXISTS(SELECT 1 FROM usuarios WHERE id_usuario = ? AND deleted_at IS NULL)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean existeUsuario(UUID idUsuario) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(EXISTS_SQL, Boolean.class, idUsuario));
    }
}
