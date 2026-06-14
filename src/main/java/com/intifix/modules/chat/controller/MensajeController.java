package com.intifix.modules.chat.controller;

import com.intifix.modules.chat.dto.request.EditarMensajeRequest;
import com.intifix.modules.chat.dto.request.EnviarMensajeRequest;
import com.intifix.modules.chat.dto.response.MensajeResponse;
import com.intifix.modules.chat.service.MensajeService;
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

import java.util.Map;
import java.util.UUID;

/**
 * Mensajes del chat. Gate de rol por endpoint + verificación de pertenencia y
 * de autoría en el servicio (anti-IDOR): el emisor siempre sale del token.
 */
@RestController
@RequestMapping("/api/v1/chat/mensajes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat - Mensajes", description = "Envío, edición e historial de mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Enviar mensaje", description = "Envía un mensaje (texto o adjunto) a una conversación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Mensaje enviado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Contenido o adjunto inválido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conversación bloqueada")
    })
    public ResponseEntity<ApiResponse<MensajeResponse>> enviar(
            @Valid @RequestBody EnviarMensajeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mensaje enviado", mensajeService.enviar(request)));
    }

    @PutMapping("/{idMensaje}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Editar mensaje", description = "Edita el contenido de un mensaje de texto propio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mensaje editado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solo se editan mensajes de texto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es el autor del mensaje"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<ApiResponse<MensajeResponse>> editar(
            @PathVariable UUID idMensaje,
            @Valid @RequestBody EditarMensajeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Mensaje editado",
                mensajeService.editar(idMensaje, request)));
    }

    @DeleteMapping("/{idMensaje}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Eliminar mensaje", description = "Elimina (soft delete) un mensaje propio")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Mensaje eliminado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es el autor del mensaje"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable UUID idMensaje) {
        mensajeService.eliminar(idMensaje);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversacion/{idConversacion}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Historial paginado", description = "Mensajes de una conversación (scroll infinito, más recientes primero)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial obtenido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<Page<MensajeResponse>>> historial(
            @PathVariable UUID idConversacion,
            @PageableDefault(size = 30, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Historial obtenido",
                mensajeService.historial(idConversacion, pageable)));
    }

    @PostMapping("/conversacion/{idConversacion}/leer")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Marcar como leída", description = "Marca como leídos los mensajes recibidos en la conversación")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mensajes marcados como leídos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<Map<String, Long>>> marcarLeida(@PathVariable UUID idConversacion) {
        return ResponseEntity.ok(ApiResponse.success("Mensajes marcados como leídos",
                Map.of("marcados", mensajeService.marcarLeida(idConversacion))));
    }

    @GetMapping("/conversacion/{idConversacion}/no-leidos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    @Operation(summary = "Contador de no leídos")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conteo obtenido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No es participante de la conversación"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNoLeidos(@PathVariable UUID idConversacion) {
        return ResponseEntity.ok(ApiResponse.success("Conteo obtenido",
                Map.of("noLeidos", mensajeService.contarNoLeidos(idConversacion))));
    }
}
