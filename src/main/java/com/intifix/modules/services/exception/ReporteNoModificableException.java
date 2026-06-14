package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a report cannot be modified.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ReporteNoModificableException extends ServicioException {

    private static final String ERROR_CODE = "REPORTE_NOT_MODIFIABLE";

    public ReporteNoModificableException(String message) {
        super(ERROR_CODE, message);
    }

    public static ReporteNoModificableException underInvestigation(UUID idReporte) {
        return new ReporteNoModificableException("No se puede eliminar un reporte en investigación: " + idReporte);
    }
}
