package com.intifix.modules.chat.dto;

import com.intifix.modules.chat.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank String conversacionId,
        @NotNull MessageType tipo,
        @Size(max = 4000) String contenido,
        @Size(max = 500) String mediaUrl,
        Double latitud,
        Double longitud,
        String replyToMessageId
) {
}
