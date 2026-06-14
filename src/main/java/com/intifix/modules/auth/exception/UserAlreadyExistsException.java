package com.intifix.modules.auth.exception;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS");
    }

    public static UserAlreadyExistsException byCorreo(String correo) {
        return new UserAlreadyExistsException("Ya existe un usuario registrado con el correo: " + correo);
    }

    public static UserAlreadyExistsException byTelefono(String telefono) {
        return new UserAlreadyExistsException("Ya existe un usuario registrado con el teléfono: " + telefono);
    }
}
