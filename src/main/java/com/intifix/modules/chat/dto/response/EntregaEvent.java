package com.intifix.modules.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/** Acuse de entrega: notifica al emisor que el otro participante recibió sus
 *  mensajes (doble check / "entregado"), aún sin haberlos leído. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaEvent {
    private UUID idConversacion;
    private UUID idUsuario;
    private Instant fecha;
}
