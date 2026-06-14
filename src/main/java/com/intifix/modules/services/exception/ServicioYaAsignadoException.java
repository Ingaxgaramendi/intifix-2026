package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to assign a technician to a service
 * that is already assigned.
 * 
 * This exception is used to prevent duplicate assignments and ensure
 * that each service has only one active technician assignment.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioYaAsignadoException extends ServicioException {

    public ServicioYaAsignadoException(String message) {
        super(message, "SERVICIO_YA_ASIGNADO");
    }

    public ServicioYaAsignadoException(String message, Throwable cause) {
        super(message, "SERVICIO_YA_ASIGNADO", cause);
    }

    public static ServicioYaAsignadoException byId(UUID idServicio) {
        return new ServicioYaAsignadoException(
            "El servicio con idServicio: " + idServicio + " ya está asignado a un técnico"
        );
    }

    public static ServicioYaAsignadoException cannotReassign(UUID idServicio) {
        return new ServicioYaAsignadoException(
            "El servicio con idServicio: " + idServicio + " no puede ser reasignado. Ya tiene un técnico asignado."
        );
    }
}
