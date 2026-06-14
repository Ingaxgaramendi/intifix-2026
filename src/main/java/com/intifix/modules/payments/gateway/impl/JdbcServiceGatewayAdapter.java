package com.intifix.modules.payments.gateway.impl;

import com.intifix.modules.payments.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcServiceGatewayAdapter implements ServiceGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT EXISTS(
                SELECT 1 FROM servicios
                WHERE id_servicio = ?
            )
            """;

    // El precio autoritativo es el de la cotización aceptada, vinculada vía la
    // asignación del servicio. El técnico también sale de la asignación.
    private static final String FIND_BY_ID_SQL = """
            SELECT
                s.id_servicio,
                s.id_cliente,
                a.id_usuario_tecnico,
                c.precio AS monto_acordado,
                CAST(s.estado AS TEXT) AS estado
            FROM servicios s
            LEFT JOIN asignaciones_servicio a ON a.id_servicio = s.id_servicio
            LEFT JOIN cotizaciones c ON c.id_cotizacion = a.id_cotizacion
            WHERE s.id_servicio = ?
            """;

    // Un servicio es pagable cuando ya tiene técnico asignado (existe asignación
    // con su cotización aceptada) y no fue cancelado.
    private static final String IS_PAYABLE_SQL = """
            SELECT EXISTS(
                SELECT 1
                FROM servicios s
                JOIN asignaciones_servicio a ON a.id_servicio = s.id_servicio
                WHERE s.id_servicio = ?
                  AND s.estado <> 'CANCELADO'
            )
            """;

    @Override
    public boolean existsById(UUID idServicio) {
        try {
            Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID_SQL, Boolean.class, idServicio);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Error checking if service exists: {}", idServicio, e);
            return false;
        }
    }

    @Override
    public Optional<ServiceInfo> findById(UUID idServicio) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                    (rs, rowNum) -> new ServiceInfo(
                            rs.getObject("id_servicio", UUID.class),
                            rs.getObject("id_cliente", UUID.class),
                            rs.getObject("id_usuario_tecnico", UUID.class),
                            rs.getBigDecimal("monto_acordado"),
                            rs.getString("estado")
                    ),
                    idServicio));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error finding service by id: {}", idServicio, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isPayable(UUID idServicio) {
        try {
            Boolean isPayable = jdbcTemplate.queryForObject(IS_PAYABLE_SQL, Boolean.class, idServicio);
            return isPayable != null && isPayable;
        } catch (Exception e) {
            log.error("Error checking if service is payable: {}", idServicio, e);
            return false;
        }
    }
}
