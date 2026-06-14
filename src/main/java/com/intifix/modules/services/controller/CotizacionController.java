package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.CrearCotizacionRequest;
import com.intifix.modules.services.dto.request.ResponderCotizacionRequest;
import com.intifix.modules.services.dto.response.CotizacionResponse;
import com.intifix.modules.services.service.CotizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Cotizacion operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services/cotizaciones")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones", description = "Operaciones de gestión de cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Crear cotización", description = "Crea una nueva cotización para un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cotización creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo técnicos pueden crear cotizaciones"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio finalizado o técnico no disponible")
    })
    public ResponseEntity<ApiResponse<CotizacionResponse>> crearCotizacion(
            @Valid @RequestBody CrearCotizacionRequest request) {
        CotizacionResponse response = cotizacionService.crearCotizacion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Cotización creada exitosamente", response));
    }

    @PatchMapping("/{idCotizacion}/responder")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Responder cotización", description = "Acepta o rechaza una cotización")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotización respondida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo clientes pueden responder cotizaciones"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cotización ya respondida")
    })
    public ResponseEntity<ApiResponse<CotizacionResponse>> responderCotizacion(
            @Parameter(description = "ID de la cotización") @PathVariable UUID idCotizacion,
            @Valid @RequestBody ResponderCotizacionRequest request) {
        CotizacionResponse response = cotizacionService.responderCotizacion(idCotizacion, request);
        return ResponseEntity.ok(ApiResponse.success("Cotización respondida exitosamente", response));
    }

    @DeleteMapping("/{idCotizacion}")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Eliminar cotización", description = "Elimina una cotización (solo si no está aceptada)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Cotización eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cotización aceptada no puede eliminarse")
    })
    public ResponseEntity<Void> eliminarCotizacion(
            @Parameter(description = "ID de la cotización") @PathVariable UUID idCotizacion) {
        cotizacionService.eliminarCotizacion(idCotizacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idCotizacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cotización por ID", description = "Obtiene una cotización por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotización obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotización no encontrada")
    })
    public ResponseEntity<ApiResponse<CotizacionResponse>> obtenerCotizacionPorId(
            @Parameter(description = "ID de la cotización") @PathVariable UUID idCotizacion) {
        CotizacionResponse response = cotizacionService.obtenerCotizacionPorId(idCotizacion);
        return ResponseEntity.ok(ApiResponse.success("Cotización obtenida exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cotizaciones por servicio", description = "Obtiene todas las cotizaciones de un servicio con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizaciones del servicio obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CotizacionResponse>>> obtenerCotizacionesPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @PageableDefault(size = 20, sort = "fechaEnvio", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CotizacionResponse> response = cotizacionService.obtenerCotizacionesPorServicio(idServicio, pageable);
        return ResponseEntity.ok(ApiResponse.success("Cotizaciones del servicio obtenidas exitosamente", response));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cotizaciones por técnico", description = "Obtiene todas las cotizaciones de un técnico con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizaciones del técnico obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CotizacionResponse>>> obtenerCotizacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico,
            @PageableDefault(size = 20, sort = "fechaEnvio", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CotizacionResponse> response = cotizacionService.obtenerCotizacionesPorTecnico(idUsuarioTecnico, pageable);
        return ResponseEntity.ok(ApiResponse.success("Cotizaciones del técnico obtenidas exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}/pendientes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cotizaciones pendientes por servicio", description = "Obtiene las cotizaciones pendientes de un servicio con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizaciones pendientes obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CotizacionResponse>>> obtenerCotizacionesPendientesPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @PageableDefault(size = 20, sort = "fechaEnvio", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CotizacionResponse> response = cotizacionService.obtenerCotizacionesPendientesPorServicio(idServicio, pageable);
        return ResponseEntity.ok(ApiResponse.success("Cotizaciones pendientes obtenidas exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}/ordenadas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cotizaciones ordenadas por precio", description = "Obtiene las cotizaciones de un servicio ordenadas por precio con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizaciones ordenadas obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CotizacionResponse>>> obtenerCotizacionesPorServicioOrdenadasPorPrecio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @Parameter(description = "Orden ascendente") @RequestParam(defaultValue = "true") boolean ascendente,
            @PageableDefault(size = 20, sort = "precio", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CotizacionResponse> response = cotizacionService.obtenerCotizacionesPorServicioOrdenadasPorPrecio(idServicio, ascendente, pageable);
        return ResponseEntity.ok(ApiResponse.success("Cotizaciones ordenadas obtenidas exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar cotizaciones por servicio", description = "Cuenta el total de cotizaciones de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de cotizaciones del servicio calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarCotizacionesPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        long total = cotizacionService.contarCotizacionesPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Total de cotizaciones del servicio calculado", total));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar cotizaciones por técnico", description = "Cuenta el total de cotizaciones de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de cotizaciones del técnico calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarCotizacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        long total = cotizacionService.contarCotizacionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Total de cotizaciones del técnico calculado", total));
    }

    @PostMapping("/expirar-vencidas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Expirar cotizaciones vencidas", description = "Marca como expiradas las cotizaciones vencidas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizaciones vencidas expiradas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo administradores")
    })
    public ResponseEntity<ApiResponse<Void>> expirarCotizacionesVencidas() {
        cotizacionService.expirarCotizacionesVencidas();
        return ResponseEntity.ok(ApiResponse.success("Cotizaciones vencidas expiradas exitosamente", null));
    }
}
