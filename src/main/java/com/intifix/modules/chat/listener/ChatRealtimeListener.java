package com.intifix.modules.chat.listener;

import com.intifix.modules.chat.event.MensajeEnviadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Entrega en tiempo real (WebSocket) los mensajes recién persistidos al
 * destinatario, desacoplando la persistencia del push. Funciona tanto para
 * envíos por REST como por STOMP, porque ambos publican el mismo evento.
 *
 * <p>Aquí también se dispararía la notificación push (Firebase) y/o su
 * persistencia en Mongo cuando el módulo de notificaciones esté disponible.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRealtimeListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void alEnviarMensaje(MensajeEnviadoEvent evento) {
        if (evento.idDestinatario() == null) {
            return;
        }
        // Cola privada del destinatario: Spring enruta /user/{id}/queue/... solo a sus sesiones.
        messagingTemplate.convertAndSendToUser(
                evento.idDestinatario().toString(),
                "/queue/messages",
                evento.mensaje());
        log.debug("Mensaje {} entregado en tiempo real a {}",
                evento.mensaje().getId(), evento.idDestinatario());
    }
}
