package com.intifix.modules.technicians.exception;

public class TecnicoException extends RuntimeException {

    private final String errorCode;

    public TecnicoException(String message) {
        super(message);
        this.errorCode = "TECNICO_ERROR";
    }

    public TecnicoException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TECNICO_ERROR";
    }

    public TecnicoException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TecnicoException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
