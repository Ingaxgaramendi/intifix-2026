package com.intifix.modules.services.gateway.impl;

import com.intifix.modules.services.gateway.TechnicianGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Real implementation of TechnicianGateway using JDBC for cross-module communication.
 * 
 * This implementation queries the technicians module tables directly within the
 * modular monolith architecture, maintaining module independence without
 * creating JPA relationships.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TechnicianGatewayImpl implements TechnicianGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_EXISTS_TECHNICIAN = """
        SELECT COUNT(*) FROM perfiles_tecnico WHERE id_usuario = ?
        """;

    private static final String SQL_IS_APPROVED = """
        SELECT COUNT(*) FROM perfiles_tecnico 
        WHERE id_usuario = ? AND estado_aprobacion = 'APROBADO'
        """;

    private static final String SQL_IS_AVAILABLE = """
        SELECT COUNT(*) FROM perfiles_tecnico 
        WHERE id_usuario = ? AND disponibilidad = 'DISPONIBLE' AND estado_aprobacion = 'APROBADO'
        """;

    private static final String SQL_GET_TECHNICIAN_LOCATION = """
        SELECT id_ubicacion FROM perfiles_tecnico WHERE id_usuario = ?
        """;

    @Override
    @Transactional(readOnly = true)
    public boolean existsTechnician(UUID idTecnico) {
        log.debug("Checking if technician exists: {}", idTecnico);
        if (idTecnico == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_TECHNICIAN, Integer.class, idTecnico);
        boolean exists = count != null && count > 0;
        log.debug("Technician exists: {} for id: {}", exists, idTecnico);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isApproved(UUID idTecnico) {
        log.debug("Checking if technician is approved: {}", idTecnico);
        if (idTecnico == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_IS_APPROVED, Integer.class, idTecnico);
        boolean isApproved = count != null && count > 0;
        log.debug("Technician is approved: {} for id: {}", isApproved, idTecnico);
        return isApproved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(UUID idTecnico) {
        log.debug("Checking if technician is available: {}", idTecnico);
        if (idTecnico == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_IS_AVAILABLE, Integer.class, idTecnico);
        boolean isAvailable = count != null && count > 0;
        log.debug("Technician is available: {} for id: {}", isAvailable, idTecnico);
        return isAvailable;
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getTechnicianLocation(UUID idTecnico) {
        log.debug("Getting technician location for: {}", idTecnico);
        if (idTecnico == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(SQL_GET_TECHNICIAN_LOCATION, UUID.class, idTecnico);
        } catch (Exception e) {
            log.debug("Técnico {} no tiene ubicación registrada: {}", idTecnico, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canServiceLocation(UUID idTecnico, UUID idUbicacion) {
        log.debug("Checking if technician {} can service location {}", idTecnico, idUbicacion);
        if (idTecnico == null || idUbicacion == null) {
            return false;
        }
        // This is a simplified implementation. In a real scenario, you would need
        // to check if the technician has the location in their service areas
        // For now, we return true if the technician exists and is approved
        return isApproved(idTecnico);
    }
}
