package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a service cannot be modified.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioNoModificableException extends ServicioException {

    private static final String ERROR_CODE = "SERVICIO_NOT_MODIFIABLE";

    public ServicioNoModificableException(String message) {
        super(ERROR_CODE, message);
    }

    public static ServicioNoModificableException byId(UUID idServicio) {
        return new ServicioNoModificableException("El servicio no puede ser modificado: " + idServicio);
    }

    public static ServicioNoModificableException finalized(UUID idServicio) {
        return new ServicioNoModificableException("No se puede modificar un servicio finalizado: " + idServicio);
    }

    public static ServicioNoModificableException transicionInvalida(UUID idServicio, Object desde, Object hacia) {
        return new ServicioNoModificableException(
            "Transición de estado no permitida para el servicio " + idServicio + ": " + desde + " -> " + hacia);
    }
}
