package com.intifix.modules.users.exception;

public class ClienteException extends RuntimeException {

    private final String errorCode;

    public ClienteException(String message) {
        super(message);
        this.errorCode = "CLIENTE_ERROR";
    }

    public ClienteException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CLIENTE_ERROR";
    }

    public ClienteException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ClienteException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
