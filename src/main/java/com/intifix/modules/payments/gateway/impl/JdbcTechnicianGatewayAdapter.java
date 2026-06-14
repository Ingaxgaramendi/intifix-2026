package com.intifix.modules.payments.gateway.impl;

import com.intifix.modules.payments.gateway.TechnicianGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcTechnicianGatewayAdapter implements TechnicianGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXISTS_BY_ID_SQL = """
            SELECT EXISTS(
                SELECT 1 FROM tecnicos 
                WHERE id_tecnico = ?
            )
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT 
                id_tecnico,
                nombre,
                email,
                banco,
                cuenta_bancaria
            FROM tecnicos
            WHERE id_tecnico = ?
            """;

    @Override
    public boolean existsById(UUID idTecnico) {
        try {
            Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID_SQL, Boolean.class, idTecnico);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Error checking if technician exists: {}", idTecnico, e);
            return false;
        }
    }

    @Override
    public Optional<TechnicianInfo> findById(UUID idTecnico) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                    (rs, rowNum) -> new TechnicianInfo(
                            rs.getObject("id_tecnico", UUID.class),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("banco"),
                            rs.getString("cuenta_bancaria")
                    ),
                    idTecnico));
        } catch (Exception e) {
            log.error("Error finding technician by id: {}", idTecnico, e);
            return Optional.empty();
        }
    }
}
