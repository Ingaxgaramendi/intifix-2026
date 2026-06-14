package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a technician is not approved.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class TecnicoNoAprobadoException extends ServicioException {

    private static final String ERROR_CODE = "TECNICO_NOT_APPROVED";

    public TecnicoNoAprobadoException(String message) {
        super(ERROR_CODE, message);
    }

    public static TecnicoNoAprobadoException byId(UUID idTecnico) {
        return new TecnicoNoAprobadoException("Técnico no aprobado: " + idTecnico);
    }
}
