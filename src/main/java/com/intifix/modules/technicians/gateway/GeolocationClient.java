package com.intifix.modules.technicians.gateway;

import java.util.UUID;

/**
 * Gateway interface for cross-module communication with the Geolocation module.
 * 
 * This interface provides a clean abstraction for the technicians module to interact
 * with location data without creating direct JPA relationships or coupling to the
 * geolocation module's internal implementation.
 * 
 * This design follows the Modular Monolith pattern and prepares for future migration
 * to microservices, where this interface would be implemented as a REST client
 * or gRPC client calling the intifix-geolocation-service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface GeolocationClient {

    /**
     * Checks if a location exists in the geolocation module.
     * 
     * This method is used to validate that a technician's assigned location
     * is valid before associating it with their profile.
     * 
     * @param idUbicacion the UUID of the location to check
     * @return true if the location exists and is active, false otherwise
     */
    boolean existsLocation(UUID idUbicacion);

    /**
     * Checks if a location exists and is active/valid for technician assignment.
     * 
     * This method provides additional validation beyond existence, checking
     * if the location is in a valid state for technician operations.
     * 
     * @param idUbicacion the UUID of the location to check
     * @return true if the location exists and is valid for assignment, false otherwise
     */
    boolean isValidLocation(UUID idUbicacion);

    /**
     * Validates that a location can be assigned to a technician.
     * 
     * This method performs comprehensive validation including:
     * - Location existence
     * - Location active status
     * - Geographic validity
     * 
     * @param idUbicacion the UUID of the location to validate
     * @return true if the location can be assigned to a technician, false otherwise
     */
    boolean canAssignToTechnician(UUID idUbicacion);
}
