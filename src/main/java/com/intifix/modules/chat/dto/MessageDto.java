package com.intifix.modules.chat.dto;

import com.intifix.modules.chat.entity.MessageStatus;
import com.intifix.modules.chat.entity.MessageType;

import java.time.Instant;

public record MessageDto(
        String id,
        String conversacionId,
        String remitenteId,
        String contenido,
        MessageType tipo,
        MessageStatus estado,
        String mediaUrl,
        Double latitud,
        Double longitud,
        String replyToMessageId,
        Instant createdAt
) {
}
