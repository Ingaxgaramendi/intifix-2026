package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a quotation is not found.
 * 
 * This exception is used when attempting to access, update, or delete
 * a quotation that does not exist in the system.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CotizacionNoEncontradaException extends ServicioException {

    public CotizacionNoEncontradaException(String message) {
        super(message, "COTIZACION_NOT_FOUND");
    }

    public CotizacionNoEncontradaException(String message, Throwable cause) {
        super(message, "COTIZACION_NOT_FOUND", cause);
    }

    public static CotizacionNoEncontradaException byId(UUID idCotizacion) {
        return new CotizacionNoEncontradaException(
            "La cotización con idCotizacion: " + idCotizacion + " no existe"
        );
    }

    public static CotizacionNoEncontradaException forService(UUID idServicio) {
        return new CotizacionNoEncontradaException(
            "No se encontraron cotizaciones para el servicio con idServicio: " + idServicio
        );
    }
}
