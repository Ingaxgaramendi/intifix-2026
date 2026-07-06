package com.intifix.modules.services.gateway.impl;

import com.intifix.modules.services.gateway.GeolocationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Real implementation of GeolocationGateway using JDBC for cross-module communication.
 * 
 * This implementation queries the geolocation module tables directly within the
 * modular monolith architecture, maintaining module independence without
 * creating JPA relationships.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeolocationGatewayImpl implements GeolocationGateway {

    private final JdbcTemplate jdbcTemplate;

    private static final String COL_LATITUD = "latitud";
    private static final String COL_LONGITUD = "longitud";

    private static final String SQL_EXISTS_LOCATION = """
        SELECT COUNT(*) FROM ubicaciones WHERE id_ubicacion = ?
        """;

    private static final String SQL_IS_VALID_LOCATION = """
        SELECT COUNT(*) FROM ubicaciones 
        WHERE id_ubicacion = ? AND latitud IS NOT NULL AND longitud IS NOT NULL
        """;

    private static final String SQL_GET_LOCATION_COORDS = """
        SELECT latitud, longitud FROM ubicaciones WHERE id_ubicacion = ?
        """;

    @Override
    @Transactional(readOnly = true)
    public boolean existsLocation(UUID idUbicacion) {
        log.debug("Checking if location exists: {}", idUbicacion);
        if (idUbicacion == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_LOCATION, Integer.class, idUbicacion);
        boolean exists = count != null && count > 0;
        log.debug("Location exists: {} for id: {}", exists, idUbicacion);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidLocation(UUID idUbicacion) {
        log.debug("Checking if location is valid: {}", idUbicacion);
        if (idUbicacion == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(SQL_IS_VALID_LOCATION, Integer.class, idUbicacion);
        boolean isValid = count != null && count > 0;
        log.debug("Location is valid: {} for id: {}", isValid, idUbicacion);
        return isValid;
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateDistance(UUID idUbicacionOrigen, UUID idUbicacionDestino) {
        log.debug("Calculating distance between {} and {}", idUbicacionOrigen, idUbicacionDestino);
        if (idUbicacionOrigen == null || idUbicacionDestino == null) {
            return 0.0;
        }

        try {
            Double[] origenCoords = jdbcTemplate.queryForObject(
                SQL_GET_LOCATION_COORDS,
                (rs, rowNum) -> {
                    double lat = rs.getDouble(COL_LATITUD);
                    double lng = rs.getDouble(COL_LONGITUD);
                    return (rs.wasNull()) ? null : new Double[]{lat, lng};
                },
                idUbicacionOrigen
            );

            Double[] destinoCoords = jdbcTemplate.queryForObject(
                SQL_GET_LOCATION_COORDS,
                (rs, rowNum) -> {
                    double lat = rs.getDouble(COL_LATITUD);
                    double lng = rs.getDouble(COL_LONGITUD);
                    return (rs.wasNull()) ? null : new Double[]{lat, lng};
                },
                idUbicacionDestino
            );

            if (origenCoords == null || destinoCoords == null) {
                log.warn("Coordenadas NULL para origen={} destino={}", idUbicacionOrigen, idUbicacionDestino);
                return -1.0;
            }

            // Haversine formula to calculate distance in kilometers
            double lat1 = Math.toRadians(origenCoords[0]);
            double lon1 = Math.toRadians(origenCoords[1]);
            double lat2 = Math.toRadians(destinoCoords[0]);
            double lon2 = Math.toRadians(destinoCoords[1]);

            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                       Math.cos(lat1) * Math.cos(lat2) *
                       Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            double distance = 6371 * c; // Earth's radius in kilometers
            log.debug("Calculated distance: {} km", distance);
            return distance;

        } catch (Exception e) {
            log.error("Error calculating distance between {} and {}", idUbicacionOrigen, idUbicacionDestino, e);
            return -1.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> findTechniciansWithinRadius(UUID idUbicacion, double radioKm) {
        log.debug("Finding technicians within {}km of location {}", radioKm, idUbicacion);
        if (idUbicacion == null) {
            return List.of();
        }

        try {
            // Get coordinates for the location
            Double[] coords = jdbcTemplate.queryForObject(
                SQL_GET_LOCATION_COORDS,
                (rs, rowNum) -> new Double[]{rs.getDouble(COL_LATITUD), rs.getDouble(COL_LONGITUD)},
                idUbicacion
            );

            if (coords == null) {
                log.warn("Could not retrieve coordinates for location: {}", idUbicacion);
                return List.of();
            }

            // This is a simplified implementation. In a real scenario, you would need
            // to query technicians based on their service areas or registered locations
            // For now, we return an empty list as technician locations are not directly
            // stored in a way that allows radius queries
            log.debug("Radius search not fully implemented - returning empty list");
            return List.of();

        } catch (Exception e) {
            log.error("Error finding technicians within radius of location {}", idUbicacion, e);
            return List.of();
        }
    }
}
