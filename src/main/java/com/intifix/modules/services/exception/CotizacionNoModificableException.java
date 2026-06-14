package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a quotation cannot be modified.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CotizacionNoModificableException extends ServicioException {

    private static final String ERROR_CODE = "COTIZACION_NOT_MODIFIABLE";

    public CotizacionNoModificableException(String message) {
        super(ERROR_CODE, message);
    }

    public static CotizacionNoModificableException accepted(UUID idCotizacion) {
        return new CotizacionNoModificableException("No se puede modificar una cotización aceptada: " + idCotizacion);
    }

    public static CotizacionNoModificableException alreadyResponded(UUID idCotizacion) {
        return new CotizacionNoModificableException("La cotización ya ha sido respondida: " + idCotizacion);
    }
}
