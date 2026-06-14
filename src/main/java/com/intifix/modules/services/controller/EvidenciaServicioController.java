package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.CrearEvidenciaRequest;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.enums.TipoArchivo;
import com.intifix.modules.services.service.EvidenciaService;
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
 * REST controller for EvidenciaServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services/evidencias")
@RequiredArgsConstructor
@Tag(name = "Evidencias", description = "Operaciones de gestión de evidencias de servicios")
public class EvidenciaServicioController {

    private final EvidenciaService evidenciaService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Crear evidencia", description = "Crea una nueva evidencia para un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Evidencia creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<ApiResponse<EvidenciaServicioResponse>> crearEvidencia(
            @Valid @RequestBody CrearEvidenciaRequest request) {
        EvidenciaServicioResponse response = evidenciaService.crearEvidencia(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Evidencia creada exitosamente", response));
    }

    @DeleteMapping("/{idEvidencia}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Eliminar evidencia", description = "Elimina una evidencia")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Evidencia eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidencia no encontrada")
    })
    public ResponseEntity<Void> eliminarEvidencia(
            @Parameter(description = "ID de la evidencia") @PathVariable UUID idEvidencia) {
        evidenciaService.eliminarEvidencia(idEvidencia);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idEvidencia}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener evidencia por ID", description = "Obtiene una evidencia por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidencia obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evidencia no encontrada")
    })
    public ResponseEntity<ApiResponse<EvidenciaServicioResponse>> obtenerEvidenciaPorId(
            @Parameter(description = "ID de la evidencia") @PathVariable UUID idEvidencia) {
        EvidenciaServicioResponse response = evidenciaService.obtenerEvidenciaPorId(idEvidencia);
        return ResponseEntity.ok(ApiResponse.success("Evidencia obtenida exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener evidencias por servicio", description = "Obtiene todas las evidencias de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidencias del servicio obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<List<EvidenciaServicioResponse>>> obtenerEvidenciasPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        List<EvidenciaServicioResponse> response = evidenciaService.obtenerEvidenciasPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Evidencias del servicio obtenidas exitosamente", response));
    }

    @GetMapping("/usuario/{subidoPor}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener evidencias por usuario", description = "Obtiene todas las evidencias subidas por un usuario")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidencias del usuario obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<List<EvidenciaServicioResponse>>> obtenerEvidenciasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable UUID subidoPor) {
        List<EvidenciaServicioResponse> response = evidenciaService.obtenerEvidenciasPorUsuario(subidoPor);
        return ResponseEntity.ok(ApiResponse.success("Evidencias del usuario obtenidas exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}/tipo/{tipoArchivo}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener evidencias por servicio y tipo", description = "Obtiene las evidencias de un servicio por tipo de archivo")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidencias por tipo obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tipo de archivo inválido")
    })
    public ResponseEntity<ApiResponse<List<EvidenciaServicioResponse>>> obtenerEvidenciasPorServicioYTipo(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @Parameter(description = "Tipo de archivo", schema = @Schema(type = "string", allowableValues = {"IMAGEN", "VIDEO", "PDF"})) @PathVariable TipoArchivo tipoArchivo) {
        List<EvidenciaServicioResponse> response = evidenciaService.obtenerEvidenciasPorServicioYTipo(idServicio, tipoArchivo);
        return ResponseEntity.ok(ApiResponse.success("Evidencias por tipo obtenidas exitosamente", response));
    }

    @GetMapping("/servicio/{idServicio}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar evidencias por servicio", description = "Cuenta el total de evidencias de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de evidencias del servicio calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarEvidenciasPorServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        long total = evidenciaService.contarEvidenciasPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Total de evidencias del servicio calculado", total));
    }

    @GetMapping("/usuario/{subidoPor}/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contar evidencias por usuario", description = "Cuenta el total de evidencias de un usuario")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de evidencias del usuario calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Long>> contarEvidenciasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable UUID subidoPor) {
        long total = evidenciaService.contarEvidenciasPorUsuario(subidoPor);
        return ResponseEntity.ok(ApiResponse.success("Total de evidencias del usuario calculado", total));
    }
}
