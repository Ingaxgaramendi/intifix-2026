package com.intifix.modules.auth.exception;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, "USER_NOT_FOUND", cause);
    }

    public static UserNotFoundException byCorreo(String correo) {
        return new UserNotFoundException("Usuario no encontrado con correo: " + correo);
    }

    public static UserNotFoundException byId(String idUsuario) {
        return new UserNotFoundException("Usuario no encontrado con ID: " + idUsuario);
    }
}
