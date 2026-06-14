package com.intifix.modules.auth.exception;

public class RefreshTokenExpiredException extends AuthException {

    public RefreshTokenExpiredException(String message) {
        super(message, "REFRESH_TOKEN_EXPIRED");
    }

    public static RefreshTokenExpiredException defaultMessage() {
        return new RefreshTokenExpiredException("El refresh token ha expirado. Inicie sesión nuevamente.");
    }
}
