package com.intifix.modules.users.exception;

import java.util.UUID;

public class ClienteYaExisteException extends ClienteException {

    public ClienteYaExisteException(String message) {
        super(message, "CLIENTE_YA_EXISTE");
    }

    public static ClienteYaExisteException byIdUsuario(UUID idUsuario) {
        return new ClienteYaExisteException("Ya existe un perfil de cliente para el usuario: " + idUsuario);
    }
}
