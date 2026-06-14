package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Payload STOMP de "escribiendo..." enviado a /app/chat.typing. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingRequest {

    @NotNull
    private UUID idConversacion;

    private boolean escribiendo;
}
