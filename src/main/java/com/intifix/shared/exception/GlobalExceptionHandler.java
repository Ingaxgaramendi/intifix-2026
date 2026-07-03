package com.intifix.shared.exception;

import com.intifix.shared.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Maneja, con MÁXIMA precedencia, los errores genéricos de entrada (validación,
 * tipo de dato, params faltantes, JSON ilegible) para devolver 400 en vez de 500.
 * Solo declara handlers ESPECÍFICOS; el "escudo final" {@code Exception} lo
 * aportan los @RestControllerAdvice de cada módulo, que conservan su lógica de
 * negocio (404/409/403 por excepción de dominio).
 */
@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
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

    // Body inválido (@Valid sobre @RequestBody): devuelve 400 con los errores de campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity < ApiResponse < Void >> handleValidation(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
            .map(GlobalExceptionHandler::formatFieldError)
            .collect(Collectors.joining("; "));
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(detalle.isBlank() ? "Datos de solicitud inválidos." : detalle));
    }

    // Validación de parámetros (@Validated en query/path params).
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity < ApiResponse < Void >> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
    }

    // Tipo de dato incorrecto en la URL (Ej: un UUID mal formado en el path/query).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity < ApiResponse < Void >> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valor válido";
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("El parámetro '" + ex.getName() + "' no es un " + tipo + " válido."));
    }

    // Falta un query param obligatorio.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity < ApiResponse < Void >> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Falta el parámetro obligatorio: '" + ex.getParameterName() + "'."));
    }

    // JSON ilegible o malformado en el body.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity < ApiResponse < Void >> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("El cuerpo de la solicitud es inválido o está mal formado."));
    }

    // Violación de restricciones de BD (UNIQUE, FK, CHECK) — nunca debe ser 500.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity < ApiResponse < Void >> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Restricción de BD violada: {}", ex.getMostSpecificCause().getMessage());
        String msg = "Operación no permitida: ya existe un registro con esos datos.";
        // Mensajes específicos según la restricción violada.
        String cause = ex.getMostSpecificCause().getMessage();
        if (cause != null) {
            if (cause.contains("uq_tecnico_dia_bloque")) {
                msg = "Ya existe un horario con esa hora de inicio para ese día.";
            } else if (cause.contains("unique") || cause.contains("duplicate") || cause.contains("ya existe")) {
                msg = "Ya existe un registro con esos datos.";
            } else if (cause.contains("foreign key") || cause.contains("referential")) {
                msg = "La referencia indicada no existe o no es válida.";
            }
        }
        return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(msg));
    }

    private static String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
