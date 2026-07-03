package com.intifix.modules.auth.exception;

import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo de errores acotado al módulo auth (basePackages). Devuelve el código
 * HTTP correcto y el mensaje real, en vez de degradar todo a 500 en el
 * GlobalExceptionHandler.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.intifix.modules.auth")
public class AuthExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflicto(AuthException ex) {
        log.warn("Conflicto de auth: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNoEncontrado(AuthException ex) {
        log.warn("Usuario no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            InvalidTokenException.class,
            RefreshTokenExpiredException.class,
    })
    public ResponseEntity<ApiResponse<Void>> handleNoAutorizado(AuthException ex) {
        log.warn("No autorizado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({AccountSuspendedException.class, AccountBannedException.class})
    public ResponseEntity<ApiResponse<Void>> handleCuentaBloqueada(AuthException ex) {
        log.warn("Cuenta bloqueada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /** Cualquier otra AuthException → 400 con el mensaje real (nunca 500 genérico). */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(AuthException ex) {
        log.warn("Error de auth: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }
}
