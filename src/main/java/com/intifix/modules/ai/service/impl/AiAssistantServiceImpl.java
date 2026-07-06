package com.intifix.modules.ai.service.impl;

import com.intifix.modules.ai.dto.ChatRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoResponse;
import com.intifix.modules.ai.memory.ConversationMemoryService;
import com.intifix.modules.ai.prompt.SystemPrompts;
import com.intifix.modules.ai.service.AiAssistantService;
import com.intifix.modules.ai.tools.ServiceTools;
import com.intifix.modules.ai.tools.TechnicianTools;
import com.intifix.modules.ai.tools.UserTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Implementación del motor de IA.
 *
 * <p>Pipeline por petición: cargar memoria (MongoDB) → construir prompt con
 * historial + herramientas (Tool Calling sobre PostgreSQL) → generar con
 * GPT-4o vía {@link ChatClient} → transmitir vía Flux → persistir el turno.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantServiceImpl implements AiAssistantService {

    private final ChatClient intifixChatClient;
    private final ConversationMemoryService memoryService;
    private final TechnicianTools technicianTools;
    private final UserTools userTools;
    private final ServiceTools serviceTools;

    @Override
    public Flux<String> chatStream(ChatRequest request) {
        String userText = construirMensajeUsuario(request);
        memoryService.guardarMensajeUsuario(request.getConversationId(), request.getMessage());

        StringBuilder acumulado = new StringBuilder();
        return prompt(request, userText)
                .stream()
                .content()
                .doOnNext(acumulado::append)
                .doOnComplete(() ->
                        memoryService.guardarMensajeAsistente(request.getConversationId(), acumulado.toString()))
                .doOnError(e -> log.error("Error generando respuesta de IA (stream): {}", e.getMessage(), e));
    }

    @Override
    public String chat(ChatRequest request) {
        String userText = construirMensajeUsuario(request);
        memoryService.guardarMensajeUsuario(request.getConversationId(), request.getMessage());

        String respuesta = prompt(request, userText)
                .call()
                .content();

        memoryService.guardarMensajeAsistente(request.getConversationId(), respuesta);
        return respuesta;
    }

    @Override
    public PresupuestoSugeridoResponse estimarPresupuesto(PresupuestoSugeridoRequest request) {
        String userContent = "Título: %s\nDescripción: %s%s".formatted(
                request.titulo(),
                request.descripcion(),
                request.especialidad() != null && !request.especialidad().isBlank()
                        ? "\nEspecialidad: " + request.especialidad()
                        : "");

        return intifixChatClient.prompt()
                .system(SystemPrompts.PRESUPUESTO_ESTIMACION)
                .user(userContent)
                .call()
                .entity(PresupuestoSugeridoResponse.class);
    }

    // ------------------------------------------------------------------ helpers

    private ChatClient.ChatClientRequestSpec prompt(ChatRequest request, String userText) {
        List<Message> historial = memoryService.cargarHistorial(request.getConversationId());
        return intifixChatClient.prompt()
                .messages(historial)
                .user(userText)
                .tools(technicianTools, userTools, serviceTools);
    }

    /**
     * Inyecta el id del cliente (si existe) como contexto para que el modelo
     * pueda invocar las herramientas de usuario.
     */
    private String construirMensajeUsuario(ChatRequest request) {
        if (request.getUserId() != null && !request.getUserId().isBlank()) {
            return "[idCliente=%s]\n%s".formatted(request.getUserId(), request.getMessage());
        }
        return request.getMessage();
    }
}
