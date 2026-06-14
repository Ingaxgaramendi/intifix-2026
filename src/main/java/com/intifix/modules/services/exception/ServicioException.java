package com.intifix.modules.services.exception;

/**
 * Base exception for all service-related exceptions in the services module.
 * 
 * This exception provides a consistent error handling mechanism with
 * error codes for client-side error handling and internationalization.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioException extends RuntimeException {

    private final String errorCode;

    public ServicioException(String message) {
        super(message);
        this.errorCode = "SERVICIO_ERROR";
    }

    public ServicioException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICIO_ERROR";
    }

    public ServicioException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServicioException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
