package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Payload STOMP enviado a /app/chat.read para marcar la conversación como leída. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarcarLeidoRequest {

    @NotNull
    private UUID idConversacion;
}
