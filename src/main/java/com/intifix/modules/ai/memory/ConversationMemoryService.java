package com.intifix.modules.ai.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Capa de memoria conversacional respaldada por MongoDB.
 *
 * <p>Carga el historial reciente como mensajes de Spring AI para enriquecer el
 * prompt, y persiste cada turno (usuario y asistente).
 */
@Service
@RequiredArgsConstructor
public class ConversationMemoryService {

    private final ConversationMessageRepository repository;

    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";

    /**
     * Devuelve el historial reciente en orden cronológico (más antiguo primero),
     * listo para inyectarse en el prompt.
     */
    public List<Message> cargarHistorial(String conversationId) {
        List<ConversationMessage> recientes =
                repository.findTop20ByConversationIdOrderByCreadoEnDesc(conversationId);
        Collections.reverse(recientes);

        List<Message> mensajes = new ArrayList<>(recientes.size());
        for (ConversationMessage m : recientes) {
            if (ROLE_ASSISTANT.equals(m.getRole())) {
                mensajes.add(new AssistantMessage(m.getContent()));
            } else {
                mensajes.add(new UserMessage(m.getContent()));
            }
        }
        return mensajes;
    }

    public void guardarMensajeUsuario(String conversationId, String content) {
        guardar(conversationId, ROLE_USER, content);
    }

    public void guardarMensajeAsistente(String conversationId, String content) {
        guardar(conversationId, ROLE_ASSISTANT, content);
    }

    public void limpiarHistorial(String conversationId) {
        repository.deleteByConversationId(conversationId);
    }

    private void guardar(String conversationId, String role, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        repository.save(ConversationMessage.builder()
                .conversationId(conversationId)
                .role(role)
                .content(content)
                .build());
    }
}
