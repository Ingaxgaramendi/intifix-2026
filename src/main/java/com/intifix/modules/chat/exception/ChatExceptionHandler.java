package com.intifix.modules.chat.exception;

import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo de errores acotado al módulo chat (basePackages) para no chocar con
 * los advices de otros módulos ni degradar AccessDeniedException a 500.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.intifix.modules.chat")
public class ChatExceptionHandler {

    @ExceptionHandler({ConversacionNoEncontradaException.class, MensajeNoEncontradoException.class})
    public ResponseEntity<ApiResponse<Void>> handleNoEncontrado(ChatException ex) {
        log.warn("Recurso de chat no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({UsuarioNoParticipanteException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAcceso(Exception ex) {
        log.warn("Acceso denegado en chat: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({ConversacionBloqueadaException.class, ConversacionDuplicadaException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflicto(ChatException ex) {
        log.warn("Conflicto en chat: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({ArchivoInvalidoException.class, ServicioInvalidoException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(ChatException ex) {
        log.warn("Solicitud inválida en chat: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }
}
