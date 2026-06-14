package com.intifix.modules.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/** Acuse de lectura: notifica que un participante leyó la conversación. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturaEvent {
    private UUID idConversacion;
    private UUID idUsuario;
    private Instant fecha;
}
