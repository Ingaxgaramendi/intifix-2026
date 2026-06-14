package com.intifix.modules.auth.exception;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, "INVALID_TOKEN", cause);
    }

    public static InvalidTokenException expired() {
        return new InvalidTokenException("El token ha expirado");
    }

    public static InvalidTokenException malformed() {
        return new InvalidTokenException("El token tiene un formato inválido");
    }

    public static InvalidTokenException unsupported() {
        return new InvalidTokenException("El token no es soportado");
    }

    public static InvalidTokenException signature() {
        return new InvalidTokenException("La firma del token es inválida");
    }
}
