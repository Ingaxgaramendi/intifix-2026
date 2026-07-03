package com.intifix.modules.services.exception;

import com.intifix.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Services module.
 * 
 * This class handles all service-specific exceptions and maps them to
 * appropriate HTTP status codes and error responses.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@RestControllerAdvice(basePackages = "com.intifix.modules.services")
@Slf4j
public class ServiceExceptionHandler {

    @ExceptionHandler(ServicioNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioNoEncontrado(ServicioNoEncontradoException ex) {
        log.warn("Servicio no encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CotizacionNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleCotizacionNoEncontrada(CotizacionNoEncontradaException ex) {
        log.warn("Cotización no encontrada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CalificacionNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleCalificacionNoEncontrada(CalificacionNoEncontradaException ex) {
        log.warn("Calificación no encontrada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ReporteNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleReporteNoEncontrado(ReporteNoEncontradoException ex) {
        log.warn("Reporte no encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AsignacionNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleAsignacionNoEncontrada(AsignacionNoEncontradaException ex) {
        log.warn("Asignación no encontrada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(EvidenciaNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleEvidenciaNoEncontrada(EvidenciaNoEncontradaException ex) {
        log.warn("Evidencia no encontrada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        log.warn("Cliente no encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TecnicoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleTecnicoNoEncontrado(TecnicoNoEncontradoException ex) {
        log.warn("Técnico no encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UbicacionNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> handleUbicacionNoEncontrada(UbicacionNoEncontradaException ex) {
        log.warn("Ubicación no encontrada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TecnicoNoAprobadoException.class)
    public ResponseEntity<ApiResponse<Void>> handleTecnicoNoAprobado(TecnicoNoAprobadoException ex) {
        log.warn("Técnico no aprobado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TecnicoNoDisponibleException.class)
    public ResponseEntity<ApiResponse<Void>> handleTecnicoNoDisponible(TecnicoNoDisponibleException ex) {
        log.warn("Técnico no disponible: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ServicioNoModificableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioNoModificable(ServicioNoModificableException ex) {
        log.warn("Servicio no modificable: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ServicioNoEliminableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioNoEliminable(ServicioNoEliminableException ex) {
        log.warn("Servicio no eliminable: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ServicioFinalizadoException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioFinalizado(ServicioFinalizadoException ex) {
        log.warn("Servicio finalizado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ServicioYaAsignadoException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioYaAsignado(ServicioYaAsignadoException ex) {
        log.warn("Servicio ya asignado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CotizacionNoModificableException.class)
    public ResponseEntity<ApiResponse<Void>> handleCotizacionNoModificable(CotizacionNoModificableException ex) {
        log.warn("Cotización no modificable: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CalificacionDuplicadaException.class)
    public ResponseEntity<ApiResponse<Void>> handleCalificacionDuplicada(CalificacionDuplicadaException ex) {
        log.warn("Calificación duplicada: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AsignacionNoModificableException.class)
    public ResponseEntity<ApiResponse<Void>> handleAsignacionNoModificable(AsignacionNoModificableException ex) {
        log.warn("Asignación no modificable: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ReporteNoModificableException.class)
    public ResponseEntity<ApiResponse<Void>> handleReporteNoModificable(ReporteNoModificableException ex) {
        log.warn("Reporte no modificable: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ServicioException.class)
    public ResponseEntity<ApiResponse<Void>> handleServicioException(ServicioException ex) {
        log.error("Error de servicio: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Error no manejado: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Error interno del servidor"));
    }
}
