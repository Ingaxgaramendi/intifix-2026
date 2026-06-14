package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.CrearCalificacionRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.service.CalificacionService;
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
 * REST controller for Calificacion operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services/calificaciones")
@RequiredArgsConstructor
@Tag(name = "Calificaciones", description = "Operaciones de gestión de calificaciones de servicios")
public class CalificacionController {

    private final CalificacionService calificacionService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear calificación", description = "Crea una nueva calificación para un servicio finalizado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Calificación creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo clientes pueden calificar"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio no finalizado o calificación duplicada")
    })
    public ResponseEntity<ApiResponse<CalificacionResponse>> crearCalificacion(
            @Valid @RequestBody CrearCalificacionRequest request) {
        CalificacionResponse response = calificacionService.crearCalificacion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Calificación creada exitosamente", response));
    }

    @DeleteMapping("/{idCalificacion}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Eliminar calificación", description = "Elimina una calificación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Calificación eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    public ResponseEntity<Void> eliminarCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable UUID idCalificacion) {
        calificacionService.eliminarCalificacion(idCalificacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idCalificacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener calificación por ID", description = "Obtiene una calificación por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calificación obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    public ResponseEntity<ApiResponse<CalificacionResponse>> obtenerCalificacionPorId(
            @Parameter(description = "ID de la calificación") @PathVariable UUID idCalificacion) {
        CalificacionResponse response = calificacionService.obtenerCalificacionPorId(idCalificacion);
        return ResponseEntity.ok(ApiResponse.success("Calificación obtenida exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener calificación por servicio", description = "Obtiene la calificación de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calificación del servicio obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    public ResponseEntity<ApiResponse<CalificacionResponse>> obtenerCalificacionPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        CalificacionResponse response = calificacionService.obtenerCalificacionPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Calificación del servicio obtenida exitosamente", response));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener calificaciones por técnico", description = "Obtiene todas las calificaciones de un técnico con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calificaciones del técnico obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CalificacionResponse>>> obtenerCalificacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico,
            @PageableDefault(size = 20, sort = "fechaCalificacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CalificacionResponse> response = calificacionService.obtenerCalificacionesPorTecnico(idUsuarioTecnico, pageable);
        return ResponseEntity.ok(ApiResponse.success("Calificaciones del técnico obtenidas exitosamente", response));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener calificaciones por cliente", description = "Obtiene todas las calificaciones de un cliente con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calificaciones del cliente obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<CalificacionResponse>>> obtenerCalificacionesPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable UUID idCliente,
            @PageableDefault(size = 20, sort = "fechaCalificacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CalificacionResponse> response = calificacionService.obtenerCalificacionesPorCliente(idCliente, pageable);
        return ResponseEntity.ok(ApiResponse.success("Calificaciones del cliente obtenidas exitosamente", response));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/promedio/puntuacion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio de puntuación de técnico", description = "Obtiene el promedio de puntuación de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedio de puntuación calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPromedioPuntuacionTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double promedio = calificacionService.obtenerPromedioPuntuacionTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Promedio de puntuación calculado", promedio));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/promedio/puntualidad")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio de puntualidad de técnico", description = "Obtiene el promedio de puntualidad de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedio de puntualidad calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPromedioPuntualidadTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double promedio = calificacionService.obtenerPromedioPuntualidadTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Promedio de puntualidad calculado", promedio));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/promedio/profesionalismo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio de profesionalismo de técnico", description = "Obtiene el promedio de profesionalismo de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedio de profesionalismo calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPromedioProfesionalismoTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double promedio = calificacionService.obtenerPromedioProfesionalismoTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Promedio de profesionalismo calculado", promedio));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/promedio/calidad-trabajo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio de calidad de trabajo de técnico", description = "Obtiene el promedio de calidad de trabajo de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedio de calidad de trabajo calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPromedioCalidadTrabajoTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double promedio = calificacionService.obtenerPromedioCalidadTrabajoTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Promedio de calidad de trabajo calculado", promedio));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/promedio/comunicacion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio de comunicación de técnico", description = "Obtiene el promedio de comunicación de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedio de comunicación calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPromedioComunicacionTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double promedio = calificacionService.obtenerPromedioComunicacionTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Promedio de comunicación calculado", promedio));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/porcentaje-recomendacion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener porcentaje de recomendación de técnico", description = "Obtiene el porcentaje de clientes que recomendarían al técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Porcentaje de recomendación calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Double>> obtenerPorcentajeRecomendacionTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        Double porcentaje = calificacionService.obtenerPorcentajeRecomendacionTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Porcentaje de recomendación calculado", porcentaje));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar calificaciones por técnico", description = "Cuenta el total de calificaciones de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de calificaciones del técnico calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarCalificacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        long total = calificacionService.contarCalificacionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Total de calificaciones del técnico calculado", total));
    }

    @GetMapping("/cliente/{idCliente}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar calificaciones por cliente", description = "Cuenta el total de calificaciones de un cliente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de calificaciones del cliente calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarCalificacionesPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable UUID idCliente) {
        long total = calificacionService.contarCalificacionesPorCliente(idCliente);
        return ResponseEntity.ok(ApiResponse.success("Total de calificaciones del cliente calculado", total));
    }
}
