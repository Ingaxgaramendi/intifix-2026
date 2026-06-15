package com.intifix.modules.audit.listener;

import com.intifix.modules.audit.entity.WebSocketAction;
import com.intifix.modules.audit.entity.WebSocketLogDocument;
import com.intifix.modules.audit.event.ChatMessageSentEvent;
import com.intifix.modules.audit.service.WebSocketLogService;
import com.intifix.shared.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.UUID;

/**
 * Registra la actividad WebSocket en {@code websocket_logs}: ciclo de la sesión
 * STOMP (conexión/desconexión, eventos nativos de Spring) y envío de mensajes
 * (evento de dominio del chat, desacoplado).
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuditListener {

    private final WebSocketLogService webSocketLogService;

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        registrar(null, extraerUserId(event.getUser()), WebSocketAction.CONNECT);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        registrar(null, extraerUserId(event.getUser()), WebSocketAction.DISCONNECT);
    }

    @EventListener
    public void onMessageSent(ChatMessageSentEvent event) {
        registrar(event.conversationId(), event.senderId(), WebSocketAction.MESSAGE_SENT);
    }

    private void registrar(UUID conversationId, UUID userId, WebSocketAction action) {
        WebSocketLogDocument log = WebSocketLogDocument.builder()
                .id(UUID.randomUUID())
                .conversationId(conversationId)
                .userId(userId)
                .action(action)
                .build();
        webSocketLogService.registrar(log);
    }

    /** El principal STOMP es un {@link AuthenticatedUser} cuyo nombre es el UUID del usuario. */
    private UUID extraerUserId(Principal principal) {
        if (principal instanceof AuthenticatedUser usuario) {
            return usuario.getId();
        }
        if (principal == null) {
            return null;
        }
        try {
            return UUID.fromString(principal.getName());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
