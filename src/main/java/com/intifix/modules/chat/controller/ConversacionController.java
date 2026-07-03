package com.intifix.modules.chat.controller;

import com.intifix.modules.chat.dto.request.CrearConversacionRequest;
import com.intifix.modules.chat.dto.request.CrearConsultaRequest;
import com.intifix.modules.chat.dto.response.ConversacionResponse;
import com.intifix.modules.chat.service.ConversacionService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Conversaciones cliente↔técnico. La autorización es en dos capas: gate de rol
 * por endpoint ({@code @PreAuthorize}) y verificación de pertenencia en el
 * servicio ({@code cargarParticipando}), que no puede expresarse en SpEL porque
 * requiere leer el documento. ADMIN queda fuera: los chats son privados.
 */
@RestController
@RequestMapping("/api/v1/chat/conversaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat - Conversaciones", description = "Gestión de conversaciones cliente↔técnico")
public class ConversacionController {

    private final ConversacionService conversacionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Crear conversación", description = "Abre la conversación cliente↔técnico de un servicio (una por servicio)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Conversación creada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Servicio inválido o sin técnico asignado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante del servicio"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ya existe conversación para el servicio")
    })
    public ResponseEntity<ApiResponse<ConversacionResponse>> crear(
            @Valid @RequestBody CrearConversacionRequest request) {
        ConversacionResponse response = conversacionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversación creada", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Mis conversaciones", description = "Inbox del usuario autenticado, ordenado por actividad reciente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversaciones obtenidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponse<Page<ConversacionResponse>>> misConversaciones(
            @PageableDefault(size = 20, sort = "actualizadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Conversaciones obtenidas",
                conversacionService.misConversaciones(pageable)));
    }

    @GetMapping("/{idConversacion}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Obtener conversación", description = "Obtiene una conversación por su UUID (solo participantes)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversación obtenida"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<ConversacionResponse>> obtener(@PathVariable UUID idConversacion) {
        return ResponseEntity.ok(ApiResponse.success("Conversación obtenida",
                conversacionService.obtenerPorId(idConversacion)));
    }

    @PatchMapping("/{idConversacion}/archivar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Archivar conversación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversación archivada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> archivar(@PathVariable UUID idConversacion) {
        conversacionService.archivar(idConversacion);
        return ResponseEntity.ok(ApiResponse.success("Conversación archivada", null));
    }

    @PatchMapping("/{idConversacion}/desarchivar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Desarchivar conversación")
    public ResponseEntity<ApiResponse<Void>> desarchivar(@PathVariable UUID idConversacion) {
        conversacionService.desarchivar(idConversacion);
        return ResponseEntity.ok(ApiResponse.success("Conversación desarchivada", null));
    }

    @PatchMapping("/{idConversacion}/bloquear")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Bloquear conversación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversación bloqueada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> bloquear(@PathVariable UUID idConversacion) {
        conversacionService.bloquear(idConversacion);
        return ResponseEntity.ok(ApiResponse.success("Conversación bloqueada", null));
    }

    @PatchMapping("/{idConversacion}/desbloquear")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Desbloquear conversación")
    public ResponseEntity<ApiResponse<Void>> desbloquear(@PathVariable UUID idConversacion) {
        conversacionService.desbloquear(idConversacion);
        return ResponseEntity.ok(ApiResponse.success("Conversación desbloqueada", null));
    }

    @PostMapping("/consulta")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Iniciar consulta", description = "Abre (o devuelve la existente) conversación de consulta con un técnico, sin servicio vinculado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Consulta creada o ya existente devuelta"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Técnico no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Solo clientes pueden iniciar consultas")
    })
    public ResponseEntity<ApiResponse<ConversacionResponse>> crearConsulta(
            @Valid @RequestBody CrearConsultaRequest request) {
        ConversacionResponse response = conversacionService.crearConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Consulta iniciada", response));
    }

    @DeleteMapping("/{idConversacion}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Eliminar conversación", description = "Elimina la conversación y sus mensajes (solo participantes)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Conversación eliminada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable UUID idConversacion) {
        conversacionService.eliminar(idConversacion);
        return ResponseEntity.noContent().build();
    }
}
