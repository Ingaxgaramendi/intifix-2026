package com.intifix.modules.chat.event;

import com.intifix.modules.chat.dto.response.MensajeResponse;

import java.util.UUID;

/**
 * Se publica al persistirse un mensaje. Un listener lo entrega en tiempo real
 * (WebSocket) al destinatario y dispara la notificación, desacoplando la
 * persistencia del push.
 */
public record MensajeEnviadoEvent(
        UUID idConversacion,
        UUID idEmisor,
        UUID idDestinatario,
        MensajeResponse mensaje
) {}
