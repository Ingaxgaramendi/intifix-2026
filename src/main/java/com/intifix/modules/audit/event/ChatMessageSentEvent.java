package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica al enviarse un mensaje de chat. Lo consume audit para registrar
 * actividad WebSocket (websocket_logs).
 */
public record ChatMessageSentEvent(
        UUID conversationId,
        UUID senderId,
        UUID recipientId,
        UUID messageId
) {}
