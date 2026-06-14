package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when an assignment cannot be modified.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class AsignacionNoModificableException extends ServicioException {

    private static final String ERROR_CODE = "ASIGNACION_NOT_MODIFIABLE";

    public AsignacionNoModificableException(String message) {
        super(ERROR_CODE, message);
    }

    public static AsignacionNoModificableException inProgress(UUID idAsignacion) {
        return new AsignacionNoModificableException("No se puede eliminar una asignación en proceso: " + idAsignacion);
    }
}
