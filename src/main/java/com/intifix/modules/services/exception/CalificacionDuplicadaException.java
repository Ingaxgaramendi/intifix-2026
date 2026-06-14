package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a duplicate rating for a service.
 * 
 * This exception is used to prevent multiple ratings for the same service,
 * ensuring that each service can only be rated once by the client.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CalificacionDuplicadaException extends ServicioException {

    public CalificacionDuplicadaException(String message) {
        super(message, "CALIFICACION_DUPLICADA");
    }

    public CalificacionDuplicadaException(String message, Throwable cause) {
        super(message, "CALIFICACION_DUPLICADA", cause);
    }

    public static CalificacionDuplicadaException byService(UUID idServicio) {
        return new CalificacionDuplicadaException(
            "El servicio con idServicio: " + idServicio + " ya tiene una calificación registrada"
        );
    }

    public static CalificacionDuplicadaException alreadyRated(UUID idServicio) {
        return new CalificacionDuplicadaException(
            "El servicio con idServicio: " + idServicio + " ya fue calificado. No se permite calificar nuevamente."
        );
    }
}
