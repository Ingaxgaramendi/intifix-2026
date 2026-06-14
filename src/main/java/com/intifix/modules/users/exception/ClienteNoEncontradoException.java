package com.intifix.modules.users.exception;

import java.util.UUID;

public class ClienteNoEncontradoException extends ClienteException {

    public ClienteNoEncontradoException(String message) {
        super(message, "CLIENTE_NOT_FOUND");
    }

    public ClienteNoEncontradoException(String message, Throwable cause) {
        super(message, "CLIENTE_NOT_FOUND", cause);
    }

    public static ClienteNoEncontradoException byIdUsuario(UUID idUsuario) {
        return new ClienteNoEncontradoException("Cliente no encontrado con idUsuario: " + idUsuario);
    }

    public static ClienteNoEncontradoException byDniRuc(String dniRuc) {
        return new ClienteNoEncontradoException("Cliente no encontrado con DNI/RUC: " + dniRuc);
    }
}
