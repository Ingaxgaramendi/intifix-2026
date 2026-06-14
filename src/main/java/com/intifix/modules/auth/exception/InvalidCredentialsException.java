package com.intifix.modules.auth.exception;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }

    public static InvalidCredentialsException defaultMessage() {
        return new InvalidCredentialsException("Credenciales inválidas");
    }
}
