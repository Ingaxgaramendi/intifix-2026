package com.intifix.modules.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intifix.modules.ai.dto.ChatRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoResponse;
import com.intifix.modules.ai.service.AiAssistantService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * API del motor de IA de IntiFix.
 *
 * <p>El endpoint de streaming usa {@link SseEmitter} (Spring MVC nativo)
 * en lugar de {@code Flux<String>}. Esto evita el conflicto entre el
 * contexto de seguridad de Spring Security (ThreadLocal) y los hilos
 * del scheduler de Reactor (boundedElastic), que causaba un
 * "Unable to handle the Spring Security Exception because the response
 * is already committed" al escribir la respuesta SSE.
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "IA", description = "Motor inteligente de recomendación de técnicos")
public class AiChatController {

    private final AiAssistantService aiAssistantService;
    private final ObjectMapper objectMapper;
    private final com.intifix.modules.ai.memory.ConversationMemoryService memoryService;

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Chat con streaming", description = "Genera la respuesta token a token vía SSE")
    public SseEmitter chatStream(@Valid @RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(180_000L); // 3 min timeout
        AtomicReference<Disposable> subRef = new AtomicReference<>();

        Disposable sub = aiAssistantService.chatStream(request)
            .subscribe(
                token -> {
                    try {
                        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(token)));
                    } catch (Exception e) {
                        log.debug("SSE write failed (client disconnected?): {}", e.getMessage());
                        emitter.completeWithError(e);
                        Disposable d = subRef.get();
                        if (d != null) d.dispose();
                    }
                },
                error -> {
                    log.error("Error en AI stream: {}", error.getMessage(), error);
                    emitter.completeWithError(error);
                },
                emitter::complete
            );

        subRef.set(sub);

        Runnable cancel = () -> {
            Disposable d = subRef.get();
            if (d != null && !d.isDisposed()) d.dispose();
        };
        emitter.onCompletion(cancel);
        emitter.onTimeout(cancel);
        emitter.onError(t -> cancel.run());

        return emitter;
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat (respuesta completa)", description = "Genera la respuesta completa en una sola llamada")
    public ResponseEntity<ApiResponse<String>> chat(@Valid @RequestBody ChatRequest request) {
        String respuesta = aiAssistantService.chat(request);
        return ResponseEntity.ok(ApiResponse.success("Respuesta generada.", respuesta));
    }

    @DeleteMapping("/chat/{conversationId}")
    @Operation(summary = "Limpiar historial", description = "Elimina el historial de una conversación en MongoDB")
    public ResponseEntity<ApiResponse<Void>> limpiarHistorial(@PathVariable String conversationId) {
        memoryService.limpiarHistorial(conversationId);
        return ResponseEntity.ok(ApiResponse.success("Historial eliminado.", null));
    }

    @PostMapping("/presupuesto-sugerido")
    @Operation(summary = "Estimar presupuesto", description = "Retorna un rango de precio estimado para el servicio descrito")
    public ResponseEntity<ApiResponse<PresupuestoSugeridoResponse>> presupuestoSugerido(
            @RequestBody PresupuestoSugeridoRequest request) {
        PresupuestoSugeridoResponse estimacion = aiAssistantService.estimarPresupuesto(request);
        return ResponseEntity.ok(ApiResponse.success("Presupuesto estimado.", estimacion));
    }
}
