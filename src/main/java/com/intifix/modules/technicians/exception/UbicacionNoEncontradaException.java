package com.intifix.modules.technicians.exception;

import java.util.UUID;

/**
 * Exception thrown when a location is not found in the geolocation module.
 * 
 * This exception is used when attempting to assign a location to a technician
 * but the location UUID does not exist or is not accessible.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class UbicacionNoEncontradaException extends TecnicoException {

    public UbicacionNoEncontradaException(String message) {
        super(message, "UBICACION_NOT_FOUND");
    }

    public UbicacionNoEncontradaException(String message, Throwable cause) {
        super(message, "UBICACION_NOT_FOUND", cause);
    }

    public static UbicacionNoEncontradaException byId(UUID idUbicacion) {
        return new UbicacionNoEncontradaException(
            "La ubicación con idUbicacion: " + idUbicacion + " no existe o no está disponible"
        );
    }

    public static UbicacionNoEncontradaException forTechnicianAssignment(UUID idUbicacion) {
        return new UbicacionNoEncontradaException(
            "No se puede asignar la ubicación con idUbicacion: " + idUbicacion + 
            " al técnico. La ubicación no existe o no está activa"
        );
    }
}
