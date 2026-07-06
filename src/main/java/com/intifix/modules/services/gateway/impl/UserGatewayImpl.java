package com.intifix.modules.services.gateway.impl;

import com.intifix.modules.services.gateway.UserGateway;
import com.intifix.modules.services.exception.ClienteNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Real implementation of UserGateway using JDBC for cross-module communication.
 * 
 * This implementation queries the users module tables directly within the
 * modular monolith architecture, maintaining module independence without
 * creating JPA relationships.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserGatewayImpl implements UserGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_EXISTS_CLIENT = """
        SELECT COUNT(*) FROM perfiles_cliente WHERE id_usuario = ?
        """;

    private static final String SQL_EXISTS_USER = """
        SELECT COUNT(*) FROM usuarios WHERE id_usuario = ?
        """;

    private static final String SQL_IS_USER_ACTIVE = """
        SELECT COUNT(*) FROM usuarios 
        WHERE id_usuario = ? AND estado = 'ACTIVO'
        """;

    private static final String SQL_GET_USER_ROLE = """
        SELECT rol FROM usuario_roles WHERE id_usuario = ? LIMIT 1
        """;

    private static final String SQL_GET_CLIENT_NAME = """
        SELECT nombres_completos FROM perfiles_cliente WHERE id_usuario = ?
        """;

    private static final String SQL_GET_CLIENT_LOCATION = """
        SELECT id_ubicacion FROM perfiles_cliente WHERE id_usuario = ?
        """;

    @Override
    @Transactional(readOnly = true)
    public boolean existsClient(UUID idCliente) {
        log.debug("Checking if client exists: {}", idCliente);
        if (idCliente == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_CLIENT, Integer.class, idCliente);
        boolean exists = count != null && count > 0;
        log.debug("Client exists: {} for id: {}", exists, idCliente);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUser(UUID idUsuario) {
        log.debug("Checking if user exists: {}", idUsuario);
        if (idUsuario == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_USER, Integer.class, idUsuario);
        boolean exists = count != null && count > 0;
        log.debug("User exists: {} for id: {}", exists, idUsuario);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(UUID idUsuario) {
        log.debug("Checking if user is active: {}", idUsuario);
        if (idUsuario == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_IS_USER_ACTIVE, Integer.class, idUsuario);
        boolean isActive = count != null && count > 0;
        log.debug("User is active: {} for id: {}", isActive, idUsuario);
        return isActive;
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserRole(UUID idUsuario) {
        log.debug("Getting user role for: {}", idUsuario);
        if (idUsuario == null) {
            return null;
        }
        String role = jdbcTemplate.queryForObject(SQL_GET_USER_ROLE, String.class, idUsuario);
        log.debug("User role: {} for id: {}", role, idUsuario);
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public String getClientName(UUID idCliente) {
        if (idCliente == null) {
            return null;
        }
        List<String> nombres = jdbcTemplate.query(
                SQL_GET_CLIENT_NAME, (rs, n) -> rs.getString(1), idCliente);
        return nombres.isEmpty() ? null : nombres.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getClientLocation(UUID idCliente) {
        if (idCliente == null) {
            return null;
        }
        try {
            List<UUID> results = jdbcTemplate.query(
                    SQL_GET_CLIENT_LOCATION, (rs, n) -> rs.getObject(1, UUID.class), idCliente);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.debug("Cliente {} no tiene ubicación registrada: {}", idCliente, e.getMessage());
            return null;
        }
    }
}
