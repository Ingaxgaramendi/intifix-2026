package com.intifix.modules.chat.gateway.impl;

import com.intifix.modules.chat.gateway.ChatGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcChatGatewayAdapter implements ChatGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXISTE_USUARIO_SQL = """
            SELECT EXISTS(
                SELECT 1 FROM usuarios WHERE id_usuario = ?
            )
            """;

    // El técnico sale de la asignación del servicio (LEFT JOIN: null si no asignado).
    private static final String PARTICIPANTES_SQL = """
            SELECT
                s.id_servicio,
                s.id_cliente,
                a.id_usuario_tecnico
            FROM servicios s
            LEFT JOIN asignaciones_servicio a ON a.id_servicio = s.id_servicio
            WHERE s.id_servicio = ?
            """;

    @Override
    public boolean existeUsuario(UUID idUsuario) {
        if (idUsuario == null) {
            return false;
        }
        try {
            Boolean existe = jdbcTemplate.queryForObject(EXISTE_USUARIO_SQL, Boolean.class, idUsuario);
            return existe != null && existe;
        } catch (Exception e) {
            log.error("Error verificando usuario {}", idUsuario, e);
            return false;
        }
    }

    @Override
    public Optional<ServicioParticipantes> obtenerParticipantes(UUID idServicio) {
        if (idServicio == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(PARTICIPANTES_SQL,
                    (rs, rowNum) -> new ServicioParticipantes(
                            rs.getObject("id_servicio", UUID.class),
                            rs.getObject("id_cliente", UUID.class),
                            rs.getObject("id_usuario_tecnico", UUID.class)
                    ),
                    idServicio));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error obteniendo participantes del servicio {}", idServicio, e);
            return Optional.empty();
        }
    }
}
