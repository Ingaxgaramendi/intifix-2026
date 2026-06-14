package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a location is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class UbicacionNoEncontradaException extends ServicioException {

    private static final String ERROR_CODE = "UBICACION_NOT_FOUND";

    public UbicacionNoEncontradaException(String message) {
        super(ERROR_CODE, message);
    }

    public static UbicacionNoEncontradaException byId(UUID idUbicacion) {
        return new UbicacionNoEncontradaException("Ubicación no encontrada: " + idUbicacion);
    }
}
