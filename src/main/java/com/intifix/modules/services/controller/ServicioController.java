package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.request.ActualizarServicioRequest;
import com.intifix.modules.services.dto.request.CambiarEstadoServicioRequest;
import com.intifix.modules.services.dto.request.CrearServicioRequest;
import com.intifix.modules.services.dto.response.ServicioDetalleResponse;
import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.gateway.TechnicianGateway;
import com.intifix.modules.services.service.ServicioService;
import com.intifix.modules.services.util.PageableUtils;
import com.intifix.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.Set;
import java.util.UUID;

/**
 * REST controller for Servicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Operaciones de gestión de servicios")
public class ServicioController {

    private final ServicioService servicioService;
    private final TechnicianGateway technicianGateway;

    /** Propiedades por las que se permite ordenar los listados de servicios. */
    private static final Set<String> ORDEN_PERMITIDO =
        Set.of("fechaCreacion", "fechaProgramada", "estado");

    /** Orden por defecto cuando el solicitado es inválido o está vacío. */
    private static final Sort ORDEN_DEFECTO = Sort.by(Sort.Direction.DESC, "fechaCreacion");

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear servicio", description = "Crea un nuevo servicio para un cliente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo clientes pueden crear servicios"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente o ubicación no encontrados")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> crearServicio(
            @Valid @RequestBody CrearServicioRequest request) {
        ServicioResponse response = servicioService.crearServicio(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Servicio creado exitosamente", response));
    }

    @PutMapping("/{idServicio}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar servicio", description = "Actualiza un servicio existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio no modificable")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> actualizarServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @Valid @RequestBody ActualizarServicioRequest request) {
        ServicioResponse response = servicioService.actualizarServicio(idServicio, request);
        return ResponseEntity.ok(ApiResponse.success("Servicio actualizado exitosamente", response));
    }

    @PatchMapping("/{idServicio}/estado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Cambiar estado del servicio", description = "Cambia el estado de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado del servicio cambiado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio no modificable")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> cambiarEstadoServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio,
            @Valid @RequestBody CambiarEstadoServicioRequest request) {
        ServicioResponse response = servicioService.cambiarEstadoServicio(idServicio, request);
        return ResponseEntity.ok(ApiResponse.success("Estado del servicio cambiado exitosamente", response));
    }

    @DeleteMapping("/{idServicio}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio (solo si está en estado PENDIENTE)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Servicio eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Servicio no eliminable")
    })
    public ResponseEntity<Void> eliminarServicio(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        servicioService.eliminarServicio(idServicio);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener servicio por ID", description = "Obtiene un servicio por su ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio obtenido exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> obtenerServicioPorId(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        ServicioResponse response = servicioService.obtenerServicioPorId(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Servicio obtenido exitosamente", response));
    }

    @GetMapping("/{idServicio}/detalle")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle del servicio", description = "Obtiene el detalle completo de un servicio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalle del servicio obtenido exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<ApiResponse<ServicioDetalleResponse>> obtenerDetalleServicioPorId(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        ServicioDetalleResponse response = servicioService.obtenerDetalleServicioPorId(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Detalle del servicio obtenido exitosamente", response));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasRole('ADMIN') or #idCliente == authentication.principal.id")
    @Operation(summary = "Obtener servicios por cliente", description = "Obtiene los servicios de un cliente con paginación. Solo el propio cliente o un ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicios del cliente obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo el propio cliente o ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> obtenerServiciosPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable UUID idCliente,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        Page<ServicioResponse> response = servicioService.obtenerServiciosPorCliente(idCliente, saneado);
        return ResponseEntity.ok(ApiResponse.success("Servicios del cliente obtenidos exitosamente", response));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Marketplace de servicios disponibles", description = "Lista los servicios abiertos (PENDIENTE/COTIZANDO) para que los técnicos puedan cotizar")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicios disponibles obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo técnicos o ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> obtenerServiciosDisponibles(
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        UUID techId = SecurityUtils.currentUserId();
        UUID idUbicacionTecnico = technicianGateway.getTechnicianLocation(techId);
        Page<ServicioResponse> response = servicioService.obtenerServiciosDisponibles(saneado, idUbicacionTecnico);
        return ResponseEntity.ok(ApiResponse.success("Servicios disponibles obtenidos exitosamente", response));
    }

    @GetMapping("/ubicacion/{idUbicacion}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener servicios por ubicación", description = "Obtiene todos los servicios de una ubicación con paginación. Solo ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicios de la ubicación obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> obtenerServiciosPorUbicacion(
            @Parameter(description = "ID de la ubicación") @PathVariable UUID idUbicacion,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        Page<ServicioResponse> response = servicioService.obtenerServiciosPorUbicacion(idUbicacion, saneado);
        return ResponseEntity.ok(ApiResponse.success("Servicios de la ubicación obtenidos exitosamente", response));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener servicios por estado", description = "Obtiene todos los servicios por estado con paginación. Solo ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicios por estado obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo ADMIN"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> obtenerServiciosPorEstado(
            @Parameter(description = "Estado del servicio", schema = @Schema(type = "string", allowableValues = {"PENDIENTE", "COTIZANDO", "ASIGNADO", "EN_PROCESO", "FINALIZADO", "CANCELADO"})) @PathVariable EstadoServicio estado,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        Page<ServicioResponse> response = servicioService.obtenerServiciosPorEstado(estado, saneado);
        return ResponseEntity.ok(ApiResponse.success("Servicios por estado obtenidos exitosamente", response));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar servicios por título", description = "Busca servicios por título con paginación. Solo ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicios encontrados"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> buscarServiciosPorTitulo(
            @Parameter(description = "Título a buscar") @RequestParam String titulo,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        Page<ServicioResponse> response = servicioService.buscarServiciosPorTitulo(titulo, saneado);
        return ResponseEntity.ok(ApiResponse.success("Servicios encontrados", response));
    }

    @GetMapping("/cliente/{idCliente}/count")
    @PreAuthorize("hasRole('ADMIN') or #idCliente == authentication.principal.id")
    @Operation(summary = "Contar servicios por cliente", description = "Cuenta el total de servicios de un cliente. Solo el propio cliente o un ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de servicios del cliente calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo el propio cliente o ADMIN")
    })
    public ResponseEntity<ApiResponse<Long>> contarServiciosPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable UUID idCliente) {
        long total = servicioService.contarServiciosPorCliente(idCliente);
        return ResponseEntity.ok(ApiResponse.success("Total de servicios del cliente calculado", total));
    }

    @GetMapping("/estado/{estado}/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Contar servicios por estado", description = "Cuenta el total de servicios por estado. Solo ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total de servicios por estado calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - solo ADMIN"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<ApiResponse<Long>> contarServiciosPorEstado(
            @Parameter(description = "Estado del servicio", schema = @Schema(type = "string", allowableValues = {"PENDIENTE", "COTIZANDO", "ASIGNADO", "EN_PROCESO", "FINALIZADO", "CANCELADO"})) @PathVariable EstadoServicio estado) {
        long total = servicioService.contarServiciosPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.success("Total de servicios por estado calculado", total));
    }

    @GetMapping("/directas")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Mis solicitudes directas", description = "Lista los servicios enviados directamente al técnico autenticado (PENDIENTE/COTIZANDO)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Solicitudes directas obtenidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Solo técnicos")
    })
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> obtenerSolicitudesDirectas(
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable saneado = PageableUtils.sanitize(pageable, ORDEN_PERMITIDO, ORDEN_DEFECTO);
        return ResponseEntity.ok(ApiResponse.success("Solicitudes directas obtenidas",
                servicioService.obtenerSolicitudesDirectas(saneado)));
    }

    @PostMapping("/directas/{idServicio}/aceptar")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Aceptar solicitud directa", description = "El técnico acepta la solicitud; se crea cotizacion y asignación automáticas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Solicitud aceptada y servicio asignado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No eres el técnico de esta solicitud"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> aceptarSolicitudDirecta(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        ServicioResponse response = servicioService.aceptarSolicitudDirecta(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Solicitud aceptada", response));
    }

    @PostMapping("/directas/{idServicio}/rechazar")
    @PreAuthorize("hasRole('TECNICO')")
    @Operation(summary = "Rechazar solicitud directa", description = "El técnico rechaza; el servicio vuelve al marketplace público")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Solicitud rechazada; servicio publicado en marketplace"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No eres el técnico de esta solicitud"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<ApiResponse<ServicioResponse>> rechazarSolicitudDirecta(
            @Parameter(description = "ID del servicio") @PathVariable UUID idServicio) {
        ServicioResponse response = servicioService.rechazarSolicitudDirecta(idServicio);
        return ResponseEntity.ok(ApiResponse.success("Solicitud rechazada; publicada en marketplace", response));
    }
}
