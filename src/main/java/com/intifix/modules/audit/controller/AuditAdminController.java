package com.intifix.modules.audit.controller;

import com.intifix.modules.audit.dto.request.AuditEventFilterRequest;
import com.intifix.modules.audit.dto.request.SecurityEventFilterRequest;
import com.intifix.modules.audit.dto.response.ApiLogResponse;
import com.intifix.modules.audit.dto.response.AuditEventResponse;
import com.intifix.modules.audit.dto.response.ExceptionLogResponse;
import com.intifix.modules.audit.dto.response.GeoLogResponse;
import com.intifix.modules.audit.dto.response.SecurityEventResponse;
import com.intifix.modules.audit.dto.response.WebSocketLogResponse;
import com.intifix.modules.audit.service.ApiLogService;
import com.intifix.modules.audit.service.AuditEventService;
import com.intifix.modules.audit.service.ExceptionLogService;
import com.intifix.modules.audit.service.GeoLogService;
import com.intifix.modules.audit.service.SecurityEventService;
import com.intifix.modules.audit.service.WebSocketLogService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Panel de consulta de auditoría y observabilidad. Solo ADMIN
 * ({@code @PreAuthorize} a nivel de clase + cadena de seguridad stateless).
 * Todos los listados son paginados ({@code Page<T>}); nunca se devuelven listas
 * completas, dado el volumen de las colecciones de auditoría.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Auditoría (Admin)", description = "Consulta de la traza de auditoría y observabilidad. Requiere rol ADMIN.")
public class AuditAdminController {

    private static final String MSG_LOGS_OBTENIDOS = "Logs obtenidos";

    private final AuditEventService auditEventService;
    private final SecurityEventService securityEventService;
    private final ExceptionLogService exceptionLogService;
    private final ApiLogService apiLogService;
    private final WebSocketLogService webSocketLogService;
    private final GeoLogService geoLogService;

    @GetMapping("/audit/events")
    @Operation(summary = "Eventos de negocio", description = "Traza de eventos de negocio (audit_events) con filtros opcionales")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Eventos obtenidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<AuditEventResponse>>> auditEvents(
            @ParameterObject @Valid AuditEventFilterRequest filtro,
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Eventos obtenidos",
                auditEventService.listar(filtro, pageable)));
    }

    @GetMapping("/security/events")
    @Operation(summary = "Eventos de seguridad", description = "Login OK/fallido, JWT inválido, acceso denegado, etc. (security_events)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Eventos obtenidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<SecurityEventResponse>>> securityEvents(
            @ParameterObject @Valid SecurityEventFilterRequest filtro,
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Eventos obtenidos",
                securityEventService.listar(filtro, pageable)));
    }

    @GetMapping("/exceptions")
    @Operation(summary = "Excepciones", description = "Errores no controlados capturados globalmente (exception_logs)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Excepciones obtenidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ExceptionLogResponse>>> exceptions(
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Excepciones obtenidas",
                exceptionLogService.listar(pageable)));
    }

    @GetMapping("/http-logs")
    @Operation(summary = "Logs HTTP", description = "Una entrada por request HTTP (api_logs, TTL 90 días)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = MSG_LOGS_OBTENIDOS),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<ApiLogResponse>>> httpLogs(
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(MSG_LOGS_OBTENIDOS,
                apiLogService.listar(pageable)));
    }

    @GetMapping("/websocket-logs")
    @Operation(summary = "Logs WebSocket", description = "Conexión/desconexión y mensajería en tiempo real (websocket_logs)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = MSG_LOGS_OBTENIDOS),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<WebSocketLogResponse>>> webSocketLogs(
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(MSG_LOGS_OBTENIDOS,
                webSocketLogService.listar(pageable)));
    }

    @GetMapping("/geo-logs")
    @Operation(summary = "Logs de geolocalización", description = "Actualizaciones de ubicación y consultas geoespaciales (geo_logs)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = MSG_LOGS_OBTENIDOS),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<Page<GeoLogResponse>>> geoLogs(
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(MSG_LOGS_OBTENIDOS,
                geoLogService.listar(pageable)));
    }
}
