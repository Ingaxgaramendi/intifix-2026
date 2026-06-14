package com.intifix.modules.technicians.exception;

import java.util.UUID;

public class TecnicoNoEncontradoException extends TecnicoException {

    public TecnicoNoEncontradoException(String message) {
        super(message, "TECNICO_NOT_FOUND");
    }

    public TecnicoNoEncontradoException(String message, Throwable cause) {
        super(message, "TECNICO_NOT_FOUND", cause);
    }

    public static TecnicoNoEncontradoException byIdUsuario(UUID idUsuario) {
        return new TecnicoNoEncontradoException("Técnico no encontrado con idUsuario: " + idUsuario);
    }

    public static TecnicoNoEncontradoException byDniRuc(String dniRuc) {
        return new TecnicoNoEncontradoException("Técnico no encontrado con DNI/RUC: " + dniRuc);
    }
}
