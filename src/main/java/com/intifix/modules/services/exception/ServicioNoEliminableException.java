package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a service cannot be deleted.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioNoEliminableException extends ServicioException {

    private static final String ERROR_CODE = "SERVICIO_NOT_DELETABLE";

    public ServicioNoEliminableException(String message) {
        super(ERROR_CODE, message);
    }

    public static ServicioNoEliminableException notPending(UUID idServicio) {
        return new ServicioNoEliminableException("Solo se pueden eliminar servicios en estado PENDIENTE: " + idServicio);
    }
}
