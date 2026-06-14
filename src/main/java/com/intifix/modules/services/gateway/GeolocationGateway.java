package com.intifix.modules.services.gateway;

import java.util.UUID;

/**
 * Gateway interface for cross-module communication with the Geolocation module.
 * 
 * This interface provides a clean abstraction for the services module to interact
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
public interface GeolocationGateway {

    /**
     * Checks if a location exists in the geolocation module.
     * 
     * This method is used to validate that a service's location
     * is valid before creating or updating a service.
     * 
     * @param idUbicacion the UUID of the location to check
     * @return true if the location exists and is active, false otherwise
     */
    boolean existsLocation(UUID idUbicacion);

    /**
     * Checks if a location is valid and active.
     * 
     * This method provides additional validation beyond existence, checking
     * if the location is in a valid state for service operations.
     * 
     * @param idUbicacion the UUID of the location to check
     * @return true if the location is valid and active, false otherwise
     */
    boolean isValidLocation(UUID idUbicacion);

    /**
     * Calculates the distance between two locations.
     * 
     * This method computes the distance in kilometers between two location UUIDs.
     * Used for proximity-based technician matching.
     * 
     * @param idUbicacionOrigen the UUID of the origin location
     * @param idUbicacionDestino the UUID of the destination location
     * @return the distance in kilometers, or -1 if calculation fails
     */
    double calculateDistance(UUID idUbicacionOrigen, UUID idUbicacionDestino);

    /**
     * Finds technicians within a specified radius of a location.
     * 
     * This method retrieves technician IDs whose primary location is within
     * the specified radius of the given location.
     * 
     * @param idUbicacion the UUID of the center location
     * @param radioKm the search radius in kilometers
     * @return a list of technician UUIDs within the radius
     */
    java.util.List<UUID> findTechniciansWithinRadius(UUID idUbicacion, double radioKm);
}
