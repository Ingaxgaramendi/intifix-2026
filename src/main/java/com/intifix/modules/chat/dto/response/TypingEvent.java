package com.intifix.modules.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Evento "escribiendo..." retransmitido al otro participante. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingEvent {
    private UUID idConversacion;
    private UUID idUsuario;
    private boolean escribiendo;
}
