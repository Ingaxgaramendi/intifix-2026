package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.CrearReporteRequest;
import com.intifix.modules.services.dto.response.ReporteResponse;
import com.intifix.modules.services.enums.EstadoReporte;
import com.intifix.modules.services.service.ReporteService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Reporte operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Operaciones de gestión de reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Crear reporte", description = "Crea un nuevo reporte")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Reporte creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<ReporteResponse>> crearReporte(
            @Valid @RequestBody CrearReporteRequest request) {
        ReporteResponse response = reporteService.crearReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Reporte creado exitosamente", response));
    }

    @PutMapping("/{idReporte}/resolver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar reporte", description = "Resuelve un reporte con resolución y acción tomada")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reporte actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo administradores"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<ApiResponse<ReporteResponse>> actualizarReporte(
            @Parameter(description = "ID del reporte") @PathVariable UUID idReporte,
            @Parameter(description = "Resolución del reporte") @RequestParam String resolucion,
            @Parameter(description = "Acción tomada") @RequestParam String accionTomada) {
        ReporteResponse response = reporteService.actualizarReporte(idReporte, resolucion, accionTomada);
        return ResponseEntity.ok(ApiResponse.success("Reporte actualizado exitosamente", response));
    }

    @DeleteMapping("/{idReporte}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar reporte", description = "Elimina un reporte")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Reporte eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reporte no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Reporte en investigación no puede eliminarse")
    })
    public ResponseEntity<Void> eliminarReporte(
            @Parameter(description = "ID del reporte") @PathVariable UUID idReporte) {
        reporteService.eliminarReporte(idReporte);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idReporte}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reporte por ID", description = "Obtiene un reporte por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reporte obtenido exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<ApiResponse<ReporteResponse>> obtenerReportePorId(
            @Parameter(description = "ID del reporte") @PathVariable UUID idReporte) {
        ReporteResponse response = reporteService.obtenerReportePorId(idReporte);
        return ResponseEntity.ok(ApiResponse.success("Reporte obtenido exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reportes por servicio", description = "Obtiene todos los reportes de un servicio con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes del servicio obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPorServicio(idServicio, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes del servicio obtenidos exitosamente", response));
    }

    @GetMapping("/reportante/{idReportante}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reportes por reportante", description = "Obtiene todos los reportes de un reportante con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes del reportante obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPorReportante(
            @Parameter(description = "ID del reportante") @PathVariable UUID idReportante,
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPorReportante(idReportante, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes del reportante obtenidos exitosamente", response));
    }

    @GetMapping("/reportado/{idReportado}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reportes por reportado", description = "Obtiene todos los reportes contra un usuario con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes del reportado obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPorReportado(
            @Parameter(description = "ID del reportado") @PathVariable UUID idReportado,
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPorReportado(idReportado, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes del reportado obtenidos exitosamente", response));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reportes por estado", description = "Obtiene todos los reportes por estado con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes por estado obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPorEstado(
            @Parameter(description = "Estado del reporte", schema = @Schema(type = "string", allowableValues = {"PENDIENTE", "EN_REVISION", "RESUELTO"})) @PathVariable EstadoReporte estado,
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPorEstado(estado, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes por estado obtenidos exitosamente", response));
    }

    @GetMapping("/tipo/{tipoReporte}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reportes por tipo", description = "Obtiene todos los reportes por tipo con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes por tipo obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPorTipo(
            @Parameter(description = "Tipo de reporte") @PathVariable String tipoReporte,
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPorTipo(tipoReporte, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes por tipo obtenidos exitosamente", response));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener reportes pendientes", description = "Obtiene todos los reportes pendientes ordenados por fecha con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes pendientes obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo administradores")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPendientes(
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPendientes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes pendientes obtenidos exitosamente", response));
    }

    @GetMapping("/pendientes/alta-prioridad")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener reportes pendientes de alta prioridad", description = "Obtiene los reportes pendientes de alta prioridad con paginación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reportes pendientes de alta prioridad obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo administradores")
    })
    public ResponseEntity<ApiResponse<Page<ReporteResponse>>> obtenerReportesPendientesAltaPrioridad(
            @PageableDefault(size = 20, sort = "fechaReporte", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ReporteResponse> response = reporteService.obtenerReportesPendientesAltaPrioridad(pageable);
        return ResponseEntity.ok(ApiResponse.success("Reportes pendientes de alta prioridad obtenidos exitosamente", response));
    }

    @GetMapping("/estado/{estado}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar reportes por estado", description = "Cuenta el total de reportes por estado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de reportes por estado calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<Long>> contarReportesPorEstado(
            @Parameter(description = "Estado del reporte", schema = @Schema(type = "string", allowableValues = {"PENDIENTE", "EN_REVISION", "RESUELTO"})) @PathVariable EstadoReporte estado) {
        long total = reporteService.contarReportesPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.success("Total de reportes por estado calculado", total));
    }

    @GetMapping("/reportante/{idReportante}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar reportes por reportante", description = "Cuenta el total de reportes de un reportante")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de reportes del reportante calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarReportesPorReportante(
            @Parameter(description = "ID del reportante") @PathVariable UUID idReportante) {
        long total = reporteService.contarReportesPorReportante(idReportante);
        return ResponseEntity.ok(ApiResponse.success("Total de reportes del reportante calculado", total));
    }

    @GetMapping("/reportado/{idReportado}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar reportes por reportado", description = "Cuenta el total de reportes contra un usuario")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de reportes del reportado calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarReportesPorReportado(
            @Parameter(description = "ID del reportado") @PathVariable UUID idReportado) {
        long total = reporteService.contarReportesPorReportado(idReportado);
        return ResponseEntity.ok(ApiResponse.success("Total de reportes del reportado calculado", total));
    }
}
