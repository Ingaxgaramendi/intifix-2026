package com.intifix.modules.notifications.listener;

import com.intifix.modules.chat.event.MensajeEnviadoEvent;
import com.intifix.modules.notifications.dto.response.NotificacionResponse;
import com.intifix.modules.notifications.entity.TipoNotificacion;
import com.intifix.modules.notifications.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Genera notificaciones automáticas a partir de eventos de dominio del chat.
 * notifications es consumidor downstream de chat (acoplamiento dirigido vía
 * eventos in-process de Spring), lo que mantiene a chat sin conocer a este módulo.
 *
 * <p>Aquí se persiste la notificación (Mongo) y se entrega en tiempo real por
 * WebSocket. El push externo (Firebase Cloud Messaging) se engancharía en este
 * mismo punto una vez configuradas las credenciales/tokens de dispositivo.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificationListener {

    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void alLlegarMensaje(MensajeEnviadoEvent evento) {
        if (evento.idDestinatario() == null) {
            return;
        }

        String cuerpo = previsualizar(evento);
        NotificacionResponse notificacion = notificacionService.notificar(
                evento.idDestinatario(),
                TipoNotificacion.MENSAJE_NUEVO,
                "Nuevo mensaje",
                cuerpo,
                evento.idConversacion());

        // Entrega en tiempo real a la cola privada del destinatario.
        messagingTemplate.convertAndSendToUser(
                evento.idDestinatario().toString(),
                "/queue/notifications",
                notificacion);

        // TODO(Firebase): enviar push a los tokens de dispositivo del destinatario.
        log.debug("Notificación de mensaje generada para {}", evento.idDestinatario());
    }

    private String previsualizar(MensajeEnviadoEvent evento) {
        var mensaje = evento.mensaje();
        if (mensaje.getTipo() != com.intifix.modules.chat.entity.TipoMensaje.TEXTO) {
            return "Te envió un archivo (" + mensaje.getTipo() + ")";
        }
        String c = mensaje.getContenido() == null ? "" : mensaje.getContenido();
        return c.length() > 120 ? c.substring(0, 120) + "…" : c;
    }
}
