package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when evidence is not found.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class EvidenciaNoEncontradaException extends ServicioException {

    private static final String ERROR_CODE = "EVIDENCIA_NOT_FOUND";

    public EvidenciaNoEncontradaException(String message) {
        super(ERROR_CODE, message);
    }

    public static EvidenciaNoEncontradaException byId(UUID idEvidencia) {
        return new EvidenciaNoEncontradaException("Evidencia no encontrada: " + idEvidencia);
    }
}
