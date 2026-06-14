package com.intifix.modules.technicians.exception;

import java.util.UUID;

public class TecnicoNoAprobadoException extends TecnicoException {

    public TecnicoNoAprobadoException(String message) {
        super(message, "TECNICO_NO_APROBADO");
    }

    public TecnicoNoAprobadoException(String message, Throwable cause) {
        super(message, "TECNICO_NO_APROBADO", cause);
    }

    public static TecnicoNoAprobadoException byIdUsuario(UUID idUsuario) {
        return new TecnicoNoAprobadoException(
            "El técnico con idUsuario: " + idUsuario + " no está aprobado. Estado actual: PENDIENTE o RECHAZADO"
        );
    }
}
