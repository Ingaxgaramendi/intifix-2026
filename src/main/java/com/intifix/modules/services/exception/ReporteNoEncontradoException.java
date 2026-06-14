package com.intifix.modules.services.exception;

import java.util.UUID;

/**
 * Exception thrown when a report is not found.
 * 
 * This exception is used when attempting to access, update, or delete
 * a report that does not exist in the system.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ReporteNoEncontradoException extends ServicioException {

    public ReporteNoEncontradoException(String message) {
        super(message, "REPORTE_NOT_FOUND");
    }

    public ReporteNoEncontradoException(String message, Throwable cause) {
        super(message, "REPORTE_NOT_FOUND", cause);
    }

    public static ReporteNoEncontradoException byId(UUID idReporte) {
        return new ReporteNoEncontradoException(
            "El reporte con idReporte: " + idReporte + " no existe"
        );
    }

    public static ReporteNoEncontradoException forService(UUID idServicio) {
        return new ReporteNoEncontradoException(
            "No se encontraron reportes para el servicio con idServicio: " + idServicio
        );
    }
}
