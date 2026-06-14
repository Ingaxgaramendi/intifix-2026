package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.AsignarTecnicoRequest;
import com.intifix.modules.services.dto.response.AsignacionServicioResponse;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.service.AsignacionServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for AsignacionServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services/asignaciones")
@RequiredArgsConstructor
@Tag(name = "Asignaciones", description = "Operaciones de gestión de asignaciones de servicios")
public class AsignacionServicioController {

    private final AsignacionServicioService asignacionServicioService;

    @PostMapping("/{idServicio}/asignar")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Asignar técnico a servicio", description = "Asigna un técnico a un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Técnico asignado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo clientes pueden asignar técnicos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio o cotización no encontrados"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio ya asignado o cotización no aceptada")
    })
    public ResponseEntity<ApiResponse<AsignacionServicioResponse>> asignarTecnico(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @Valid @RequestBody AsignarTecnicoRequest request) {
        AsignacionServicioResponse response = asignacionServicioService.asignarTecnico(idServicio, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Técnico asignado exitosamente", response));
    }

    @PutMapping("/{idAsignacion}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar asignación", description = "Actualiza una asignación existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignación actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    public ResponseEntity<ApiResponse<AsignacionServicioResponse>> actualizarAsignacion(
            @Parameter(description = "ID de la asignación") @PathVariable UUID idAsignacion,
            @Valid @RequestBody AsignarTecnicoRequest request) {
        AsignacionServicioResponse response = asignacionServicioService.actualizarAsignacion(idAsignacion, request);
        return ResponseEntity.ok(ApiResponse.success("Asignación actualizada exitosamente", response));
    }

    @DeleteMapping("/{idAsignacion}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Eliminar asignación", description = "Elimina una asignación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Asignación eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Asignación en proceso no puede eliminarse")
    })
    public ResponseEntity<Void> eliminarAsignacion(
            @Parameter(description = "ID de la asignación") @PathVariable UUID idAsignacion) {
        asignacionServicioService.eliminarAsignacion(idAsignacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idAsignacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asignación por ID", description = "Obtiene una asignación por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignación obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    public ResponseEntity<ApiResponse<AsignacionServicioResponse>> obtenerAsignacionPorId(
            @Parameter(description = "ID de la asignación") @PathVariable UUID idAsignacion) {
        AsignacionServicioResponse response = asignacionServicioService.obtenerAsignacionPorId(idAsignacion);
        return ResponseEntity.ok(ApiResponse.success("Asignación obtenida exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asignación por servicio", description = "Obtiene la asignación de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignación del servicio obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    public ResponseEntity<ApiResponse<AsignacionServicioResponse>> obtenerAsignacionPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        AsignacionServicioResponse response = asignacionServicioService.obtenerAsignacionPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Asignación del servicio obtenida exitosamente", response));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asignaciones por técnico", description = "Obtiene todas las asignaciones de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignaciones del técnico obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<List<AsignacionServicioResponse>>> obtenerAsignacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        List<AsignacionServicioResponse> response = asignacionServicioService.obtenerAsignacionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Asignaciones del técnico obtenidas exitosamente", response));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asignaciones por estado", description = "Obtiene todas las asignaciones por estado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignaciones por estado obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<List<AsignacionServicioResponse>>> obtenerAsignacionesPorEstado(
            @Parameter(description = "Estado de la asignación", schema = @Schema(type = "string", allowableValues = {"ASIGNADO", "EN_PROCESO", "FINALIZADO"})) @PathVariable EstadoServicio estado) {
        List<AsignacionServicioResponse> response = asignacionServicioService.obtenerAsignacionesPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.success("Asignaciones por estado obtenidas exitosamente", response));
    }

    @PatchMapping("/{idAsignacion}/iniciar")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Iniciar servicio", description = "Marca un servicio como iniciado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio iniciado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo técnicos pueden iniciar servicios"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> iniciarServicio(
            @Parameter(description = "ID de la asignación") @PathVariable UUID idAsignacion) {
        asignacionServicioService.iniciarServicio(idAsignacion);
        return ResponseEntity.ok(ApiResponse.success("Servicio iniciado exitosamente", null));
    }

    @PatchMapping("/{idAsignacion}/finalizar")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Finalizar servicio", description = "Marca un servicio como finalizado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio finalizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo técnicos pueden finalizar servicios"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> finalizarServicio(
            @Parameter(description = "ID de la asignación") @PathVariable UUID idAsignacion) {
        asignacionServicioService.finalizarServicio(idAsignacion);
        return ResponseEntity.ok(ApiResponse.success("Servicio finalizado exitosamente", null));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar asignaciones por técnico", description = "Cuenta el total de asignaciones de un técnico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de asignaciones del técnico calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarAsignacionesPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable UUID idUsuarioTecnico) {
        long total = asignacionServicioService.contarAsignacionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Total de asignaciones del técnico calculado", total));
    }

    @GetMapping("/estado/{estado}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar asignaciones por estado", description = "Cuenta el total de asignaciones por estado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de asignaciones por estado calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<Long>> contarAsignacionesPorEstado(
            @Parameter(description = "Estado de la asignación", schema = @Schema(type = "string", allowableValues = {"ASIGNADO", "EN_PROCESO", "FINALIZADO"})) @PathVariable EstadoServicio estado) {
        long total = asignacionServicioService.contarAsignacionesPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.success("Total de asignaciones por estado calculado", total));
    }
}
