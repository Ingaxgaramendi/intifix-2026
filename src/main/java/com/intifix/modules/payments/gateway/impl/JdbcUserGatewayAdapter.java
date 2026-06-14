package com.intifix.modules.payments.gateway.impl;

import com.intifix.modules.payments.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcUserGatewayAdapter implements UserGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT EXISTS(
                SELECT 1 FROM usuarios 
                WHERE id_usuario = ?
            )
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT 
                id_usuario,
                nombre,
                email,
                tipo_documento,
                numero_documento
            FROM usuarios
            WHERE id_usuario = ?
            """;

    @Override
    public boolean existsById(UUID idUsuario) {
        try {
            Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID_SQL, Boolean.class, idUsuario);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Error checking if user exists: {}", idUsuario, e);
            return false;
        }
    }

    @Override
    public Optional<UserInfo> findById(UUID idUsuario) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                    (rs, rowNum) -> new UserInfo(
                            rs.getObject("id_usuario", UUID.class),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("tipo_documento"),
                            rs.getString("numero_documento")
                    ),
                    idUsuario));
        } catch (Exception e) {
            log.error("Error finding user by id: {}", idUsuario, e);
            return Optional.empty();
        }
    }
}
