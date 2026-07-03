package com.intifix.modules.ai.controller;

import com.intifix.modules.ai.dto.ChatRequest;
import com.intifix.modules.ai.service.AiAssistantService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * API del motor de IA de IntiFix.
 *
 * <p>El endpoint de streaming devuelve un {@code Flux<String>} sobre SSE
 * ({@code text/event-stream}); Spring MVC adapta el tipo reactivo y emite los
 * tokens a medida que llegan del modelo.
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "IA", description = "Motor inteligente de recomendación de técnicos")
public class AiChatController {

    private final AiAssistantService aiAssistantService;

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Chat con streaming", description = "Genera la respuesta token a token vía SSE")
    public Flux<String> chatStream(@Valid @RequestBody ChatRequest request) {
        return aiAssistantService.chatStream(request);
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat (respuesta completa)", description = "Genera la respuesta completa en una sola llamada")
    public ResponseEntity<ApiResponse<String>> chat(@Valid @RequestBody ChatRequest request) {
        String respuesta = aiAssistantService.chat(request);
        return ResponseEntity.ok(ApiResponse.success("Respuesta generada.", respuesta));
    }
}
