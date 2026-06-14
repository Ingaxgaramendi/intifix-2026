package com.intifix.modules.technicians.exception;

import java.util.UUID;

/**
 * Exception thrown when a location is invalid for technician assignment.
 * 
 * This exception is used when a location exists but cannot be assigned to a technician
 * due to validation rules (inactive, outside service area, etc.).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class UbicacionInvalidaException extends TecnicoException {

    public UbicacionInvalidaException(String message) {
        super(message, "UBICACION_INVALIDA");
    }

    public UbicacionInvalidaException(String message, Throwable cause) {
        super(message, "UBICACION_INVALIDA", cause);
    }

    public static UbicacionInvalidaException byId(UUID idUbicacion) {
        return new UbicacionInvalidaException(
            "La ubicación con idUbicacion: " + idUbicacion + " no es válida para asignación a técnico"
        );
    }

    public static UbicacionInvalidaException inactive(UUID idUbicacion) {
        return new UbicacionInvalidaException(
            "La ubicación con idUbicacion: " + idUbicacion + " no está activa y no puede ser asignada"
        );
    }

    public static UbicacionInvalidaException outsideServiceArea(UUID idUbicacion) {
        return new UbicacionInvalidaException(
            "La ubicación con idUbicacion: " + idUbicacion + " está fuera del área de servicio"
        );
    }

    public static UbicacionInvalidaException notValidForAssignment(UUID idUbicacion) {
        return new UbicacionInvalidaException(
            "La ubicación con idUbicacion: " + idUbicacion + " no cumple los requisitos para asignación a técnico"
        );
    }
}
