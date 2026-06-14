package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a technician is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class TecnicoNoEncontradoException extends ServicioException {

    private static final String ERROR_CODE = "TECNICO_NOT_FOUND";

    public TecnicoNoEncontradoException(String message) {
        super(ERROR_CODE, message);
    }

    public static TecnicoNoEncontradoException byId(UUID idTecnico) {
        return new TecnicoNoEncontradoException("Técnico no encontrado: " + idTecnico);
    }
}
