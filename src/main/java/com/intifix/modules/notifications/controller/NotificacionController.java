package com.intifix.modules.notifications.controller;

import com.intifix.modules.notifications.dto.response.NotificacionResponse;
import com.intifix.modules.notifications.service.NotificacionService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Bandeja de notificaciones. Cualquier rol autenticado recibe notificaciones,
 * por eso el gate es {@code isAuthenticated()}; la propiedad (solo las tuyas) se
 * fuerza en el servicio scopeando por el usuario del token (anti-IDOR): el
 * acceso a una notificación ajena devuelve 404.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notificaciones", description = "Bandeja de notificaciones del usuario")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mis notificaciones", description = "Bandeja paginada del usuario autenticado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificaciones obtenidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<NotificacionResponse>>> mis(
            @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas",
                notificacionService.misNotificaciones(pageable)));
    }

    @GetMapping("/no-leidas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mis notificaciones no leídas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "No leídas obtenidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<NotificacionResponse>>> noLeidas(
            @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("No leídas obtenidas",
                notificacionService.misNoLeidas(pageable)));
    }

    @GetMapping("/contador")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Contador de no leídas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conteo obtenido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Map<String, Long>>> contador() {
        return ResponseEntity.ok(ApiResponse.success("Conteo obtenido",
                Map.of("noLeidas", notificacionService.contarNoLeidas())));
    }

    @PatchMapping("/{idNotificacion}/leer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Marcar notificación como leída")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notificación no encontrada o ajena")
    })
    public ResponseEntity<ApiResponse<Void>> leer(@PathVariable UUID idNotificacion) {
        notificacionService.marcarLeida(idNotificacion);
        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
    }

    @PatchMapping("/leer-todas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Marcar todas como leídas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notificaciones marcadas como leídas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Map<String, Long>>> leerTodas() {
        return ResponseEntity.ok(ApiResponse.success("Notificaciones marcadas como leídas",
                Map.of("marcadas", notificacionService.marcarTodasLeidas())));
    }

    @DeleteMapping("/{idNotificacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Eliminar notificación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Notificación eliminada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notificación no encontrada o ajena")
    })
    public ResponseEntity<Void> eliminar(@PathVariable UUID idNotificacion) {
        notificacionService.eliminar(idNotificacion);
        return ResponseEntity.noContent().build();
    }
}
