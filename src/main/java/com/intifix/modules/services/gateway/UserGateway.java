package com.intifix.modules.services.gateway;

import java.util.UUID;

/**
 * Gateway interface for cross-module communication with the Users module.
 * 
 * This interface provides a clean abstraction for the services module to interact
 * with user data without creating direct JPA relationships or coupling to the
 * users module's internal implementation.
 * 
 * This design follows the Modular Monolith pattern and prepares for future migration
 * to microservices, where this interface would be implemented as a REST client
 * or gRPC client calling the intifix-user-service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface UserGateway {

    /**
     * Checks if a client exists in the users module.
     * 
     * This method is used to validate that a service's associated client
     * is valid before creating or updating a service.
     * 
     * @param idCliente the UUID of the client to check
     * @return true if the client exists and is active, false otherwise
     */
    boolean existsClient(UUID idCliente);

    /**
     * Checks if a user exists in the users module.
     * 
     * This method is used to validate that a user exists before performing
     * operations that require a valid user reference.
     * 
     * @param idUsuario the UUID of the user to check
     * @return true if the user exists and is active, false otherwise
     */
    boolean existsUser(UUID idUsuario);

    /**
     * Checks if a user is active and in good standing.
     * 
     * This method provides additional validation beyond existence, checking
     * if the user is in a valid state for service operations.
     * 
     * @param idUsuario the UUID of the user to check
     * @return true if the user is active and in good standing, false otherwise
     */
    boolean isUserActive(UUID idUsuario);

    /**
     * Gets the role of a user.
     *
     * This method retrieves the user's role to validate permissions
     * and ensure proper authorization for service operations.
     *
     * @param idUsuario the UUID of the user
     * @return the user's role as a string, or null if not found
     */
    String getUserRole(UUID idUsuario);

    /**
     * Gets the full name of a client (perfiles_cliente.nombres_completos).
     *
     * Used to enrich service listings shown to technicians with the requesting
     * client's name, without exposing the full client profile.
     *
     * @param idCliente the UUID of the client
     * @return the client's full name, or null if not found
     */
    String getClientName(UUID idCliente);
}
