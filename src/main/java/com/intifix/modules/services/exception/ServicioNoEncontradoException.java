package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a service is not found.
 * 
 * This exception is used when attempting to access, update, or delete
 * a service that does not exist in the system.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioNoEncontradoException extends ServicioException {

    public ServicioNoEncontradoException(String message) {
        super(message, "SERVICIO_NOT_FOUND");
    }

    public ServicioNoEncontradoException(String message, Throwable cause) {
        super(message, "SERVICIO_NOT_FOUND", cause);
    }

    public static ServicioNoEncontradoException byId(UUID idServicio) {
        return new ServicioNoEncontradoException(
            "El servicio con idServicio: " + idServicio + " no existe"
        );
    }

    public static ServicioNoEncontradoException forClient(UUID idCliente) {
        return new ServicioNoEncontradoException(
            "No se encontraron servicios para el cliente con idCliente: " + idCliente
        );
    }
}
