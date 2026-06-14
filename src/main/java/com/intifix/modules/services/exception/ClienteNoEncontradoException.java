package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a client is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ClienteNoEncontradoException extends ServicioException {

    private static final String ERROR_CODE = "CLIENTE_NOT_FOUND";

    public ClienteNoEncontradoException(String message) {
        super(ERROR_CODE, message);
    }

    public static ClienteNoEncontradoException byId(UUID idCliente) {
        return new ClienteNoEncontradoException("Cliente no encontrado: " + idCliente);
    }
}
