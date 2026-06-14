package com.intifix.modules.users.exception;

import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo de errores propio del módulo users. Scoped por paquete para que el
 * módulo sea autocontenido y extraíble como microservicio sin depender del
 * handler global del monolito.
 */
@RestControllerAdvice(basePackages = "com.intifix.modules.users")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ClienteExceptionHandler {

    @ExceptionHandler({ClienteNoEncontradoException.class, UsuarioNoExisteException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ClienteException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({ClienteYaExisteException.class, DniDuplicadoException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflict(ClienteException ex) {
        return buildError(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(ClienteException.class)
    public ResponseEntity<ApiResponse<Void>> handleClienteException(ClienteException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.debug("Request inválido en módulo users: {}", errores.keySet());
        return ResponseEntity.badRequest()
            .body(ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("La solicitud contiene campos inválidos.")
                .data(errores)
                .build());
    }

    /**
     * Red de seguridad ante condiciones de carrera: dos requests concurrentes
     * pueden pasar la pre-validación y chocar contra el unique de dni_ruc o la PK.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad en módulo users: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("El recurso entra en conflicto con un registro existente."));
    }

    private ResponseEntity<ApiResponse<Void>> buildError(HttpStatus status, ClienteException ex) {
        log.debug("Error de negocio [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(status).body(ApiResponse.error(ex.getMessage()));
    }
}
