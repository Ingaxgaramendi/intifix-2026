package com.intifix.modules.audit.dto.response;

import com.intifix.modules.audit.entity.WebSocketAction;

import java.time.Instant;
import java.util.UUID;

public record WebSocketLogResponse(
        UUID id,
        UUID conversationId,
        UUID userId,
        WebSocketAction action,
        Instant timestamp
) {}
