package com.intifix.modules.technicians.gateway.impl;

import com.intifix.modules.technicians.gateway.GeolocationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock implementation of GeolocationClient for development.
 * 
 * In production, this would be replaced with a REST client or gRPC client
 * that communicates with the intifix-geolocation-service microservice.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Component
@Slf4j
public class GeolocationClientImpl implements GeolocationClient {

    @Override
    public boolean existsLocation(UUID idUbicacion) {
        log.debug("Checking if location exists: {}", idUbicacion);
        // Stub: delegates to geolocation module when deployed as microservice.
        return true;
    }

    @Override
    public boolean isValidLocation(UUID idUbicacion) {
        log.debug("Checking if location is valid: {}", idUbicacion);
        // Stub: delegates to geolocation module when deployed as microservice.
        return true;
    }

    @Override
    public boolean canAssignToTechnician(UUID idUbicacion) {
        log.debug("Checking if location can be assigned to technician: {}", idUbicacion);
        // Stub: delegates to geolocation module when deployed as microservice.
        return true;
    }
}
