package com.intifix.modules.technicians.exception;

import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.intifix.modules.technicians")
public class TecnicoExceptionHandler {

    @ExceptionHandler(TecnicoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoEncontrado(TecnicoNoEncontradoException ex) {
        log.warn("Técnico no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(HorarioDuplicadoException.class)
    public ResponseEntity<ApiResponse<Void>> handleHorarioDuplicado(HorarioDuplicadoException ex) {
        log.warn("Horario solapado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TecnicoException.class)
    public ResponseEntity<ApiResponse<Void>> handleTecnico(TecnicoException ex) {
        log.warn("Error de técnico [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Error inesperado en módulo técnicos: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno: " + ex.getClass().getSimpleName() + " — " + ex.getMessage()));
    }
}
