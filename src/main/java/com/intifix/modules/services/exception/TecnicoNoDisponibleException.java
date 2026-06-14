package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a technician is not available.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class TecnicoNoDisponibleException extends ServicioException {

    private static final String ERROR_CODE = "TECNICO_NOT_AVAILABLE";

    public TecnicoNoDisponibleException(String message) {
        super(ERROR_CODE, message);
    }

    public static TecnicoNoDisponibleException byId(UUID idTecnico) {
        return new TecnicoNoDisponibleException("Técnico no disponible: " + idTecnico);
    }
}
