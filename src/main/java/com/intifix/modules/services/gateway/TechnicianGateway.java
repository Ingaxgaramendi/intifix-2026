package com.intifix.modules.services.gateway;

import java.util.UUID;

/**
 * Gateway interface for cross-module communication with the Technicians module.
 * 
 * This interface provides a clean abstraction for the services module to interact
 * with technician data without creating direct JPA relationships or coupling to the
 * technicians module's internal implementation.
 * 
 * This design follows the Modular Monolith pattern and prepares for future migration
 * to microservices, where this interface would be implemented as a REST client
 * or gRPC client calling the intifix-technician-service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface TechnicianGateway {

    /**
     * Checks if a technician exists in the technicians module.
     * 
     * This method is used to validate that a technician exists before
     * creating quotations or assigning services.
     * 
     * @param idTecnico the UUID of the technician to check
     * @return true if the technician exists, false otherwise
     */
    boolean existsTechnician(UUID idTecnico);

    /**
     * Checks if a technician is approved to offer services.
     * 
     * This method validates that a technician has completed the approval
     * process and is authorized to receive service assignments.
     * 
     * @param idTecnico the UUID of the technician to check
     * @return true if the technician is approved, false otherwise
     */
    boolean isApproved(UUID idTecnico);

    /**
     * Checks if a technician is currently available for new assignments.
     * 
     * This method checks the technician's availability status to ensure
     * they can accept new service requests.
     * 
     * @param idTecnico the UUID of the technician to check
     * @return true if the technician is available, false otherwise
     */
    boolean isAvailable(UUID idTecnico);

    /**
     * Gets the location of a technician.
     * 
     * This method retrieves the technician's primary location for
     * proximity calculations and service matching.
     * 
     * @param idTecnico the UUID of the technician
     * @return the UUID of the technician's location, or null if not set
     */
    UUID getTechnicianLocation(UUID idTecnico);

    /**
     * Checks if a technician can accept a service at a specific location.
     * 
     * This method validates that a technician's service area includes
     * the specified location.
     * 
     * @param idTecnico the UUID of the technician
     * @param idUbicacion the UUID of the service location
     * @return true if the technician can service this location, false otherwise
     */
    boolean canServiceLocation(UUID idTecnico, UUID idUbicacion);
}
