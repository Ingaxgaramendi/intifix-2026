package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when an assignment is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class AsignacionNoEncontradaException extends ServicioException {

    private static final String ERROR_CODE = "ASIGNACION_NOT_FOUND";

    public AsignacionNoEncontradaException(String message) {
        super(ERROR_CODE, message);
    }

    public static AsignacionNoEncontradaException byId(UUID idAsignacion) {
        return new AsignacionNoEncontradaException("Asignación no encontrada: " + idAsignacion);
    }

    public static AsignacionNoEncontradaException byServicio(UUID idServicio) {
        return new AsignacionNoEncontradaException("No existe asignación para el servicio: " + idServicio);
    }
}
