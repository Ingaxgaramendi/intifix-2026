package com.intifix.modules.users.exception;

import java.util.UUID;

public class UsuarioNoExisteException extends ClienteException {

    public UsuarioNoExisteException(String message) {
        super(message, "USUARIO_NO_EXISTE");
    }

    public static UsuarioNoExisteException byId(UUID idUsuario) {
        return new UsuarioNoExisteException(
            "No existe un usuario registrado con id: " + idUsuario);
    }
}
