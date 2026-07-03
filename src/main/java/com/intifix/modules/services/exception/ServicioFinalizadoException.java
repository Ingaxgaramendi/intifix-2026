package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to modify a service that is already finished.
 * 
 * This exception is used to prevent modifications to completed services,
 * ensuring data integrity and preventing post-completion changes.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioFinalizadoException extends ServicioException {

    private static final String PREFIJO = "El servicio con idServicio: ";

    public ServicioFinalizadoException(String message) {
        super(message, "SERVICIO_FINALIZADO");
    }

    public ServicioFinalizadoException(String message, Throwable cause) {
        super(message, "SERVICIO_FINALIZADO", cause);
    }

    public static ServicioFinalizadoException byId(UUID idServicio) {
        return new ServicioFinalizadoException(
            PREFIJO + idServicio + " ya está finalizado y no puede ser modificado"
        );
    }

    public static ServicioFinalizadoException cannotAssign(UUID idServicio) {
        return new ServicioFinalizadoException(
            PREFIJO + idServicio + " está finalizado. No se pueden asignar técnicos."
        );
    }

    public static ServicioFinalizadoException cannotQuote(UUID idServicio) {
        return new ServicioFinalizadoException(
            PREFIJO + idServicio + " está finalizado. No se pueden enviar cotizaciones."
        );
    }
}
