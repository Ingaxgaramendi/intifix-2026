package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a rating is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CalificacionNoEncontradaException extends ServicioException {

    private static final String ERROR_CODE = "CALIFICACION_NOT_FOUND";

    public CalificacionNoEncontradaException(String message) {
        super(ERROR_CODE, message);
    }

    public static CalificacionNoEncontradaException byId(UUID idCalificacion) {
        return new CalificacionNoEncontradaException("Calificación no encontrada: " + idCalificacion);
    }

    public static CalificacionNoEncontradaException byServicio(UUID idServicio) {
        return new CalificacionNoEncontradaException("No existe calificación para el servicio: " + idServicio);
    }
}
