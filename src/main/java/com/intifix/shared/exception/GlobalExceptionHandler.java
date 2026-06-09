package com.intifix.shared.exception;

import com.intifix.shared.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de recursos no encontrados (Ej: Técnico no existe)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity < ApiResponse < Void >> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
    }

    // Manejo de errores de negocio genéricos
    @ExceptionHandler(CustomException.class)
    public ResponseEntity < ApiResponse < Void >> handleCustomException(CustomException ex) {
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
    }

    // Escudo final contra caídas críticas del sistema (Runtime Errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity < ApiResponse < Void >> handleGlobalException(Exception ex) {
        // Aquí podrías usar un Logger para registrar el error real en MongoDB (logs_errores)
        return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("Ocurrió un error interno en el servidor de INTIFIX. Inténtalo más tarde."));
    }
}
