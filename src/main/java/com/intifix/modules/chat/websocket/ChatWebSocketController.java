package com.intifix.modules.chat.websocket;

import com.intifix.modules.chat.dto.request.EnviarMensajeRequest;
import com.intifix.modules.chat.dto.request.MarcarLeidoRequest;
import com.intifix.modules.chat.dto.request.TypingRequest;
import com.intifix.modules.chat.dto.response.LecturaEvent;
import com.intifix.modules.chat.dto.response.PresenciaResponse;
import com.intifix.modules.chat.dto.response.TypingEvent;
import com.intifix.modules.chat.entity.ConversacionDocument;
import com.intifix.modules.chat.service.ConversacionService;
import com.intifix.modules.chat.service.MensajeService;
import com.intifix.modules.chat.service.PresenciaService;
import com.intifix.shared.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

/**
 * Endpoints STOMP del chat. Destinos de aplicación bajo el prefijo /app:
 * <ul>
 *   <li>/app/chat.send     → enviar mensaje</li>
 *   <li>/app/chat.read     → marcar conversación como leída (acuse)</li>
 *   <li>/app/chat.typing   → "escribiendo..."</li>
 *   <li>/app/chat.online   → marcar presencia online</li>
 *   <li>/app/chat.offline  → marcar presencia offline</li>
 * </ul>
 *
 * <p>El push al destinatario va por su cola privada {@code /user/{id}/queue/...}.
 * Como el {@code SecurityContextHolder} no se propaga al hilo de mensajería, se
 * reconstruye desde el principal del STOMP para que los servicios (que usan
 * {@code SecurityUtils}) operen igual que en REST.</p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MensajeService mensajeService;
    private final ConversacionService conversacionService;
    private final PresenciaService presenciaService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void enviar(@Valid @Payload EnviarMensajeRequest request, Principal principal) {
        // enviar() publica el evento; ChatRealtimeListener entrega al destinatario.
        conContexto(principal, () -> mensajeService.enviar(request));
    }

    @MessageMapping("/chat.read")
    public void marcarLeido(@Valid @Payload MarcarLeidoRequest request, Principal principal) {
        UUID userId = idUsuario(principal);
        conContexto(principal, () -> mensajeService.marcarLeida(request.getIdConversacion()));

        UUID otro = otroParticipante(request.getIdConversacion(), userId);
        if (otro != null) {
            messagingTemplate.convertAndSendToUser(otro.toString(), "/queue/read",
                    LecturaEvent.builder()
                            .idConversacion(request.getIdConversacion())
                            .idUsuario(userId)
                            .fecha(Instant.now())
                            .build());
        }
    }

    @MessageMapping("/chat.typing")
    public void escribiendo(@Valid @Payload TypingRequest request, Principal principal) {
        UUID userId = idUsuario(principal);
        UUID otro = otroParticipante(request.getIdConversacion(), userId);
        if (otro != null) {
            messagingTemplate.convertAndSendToUser(otro.toString(), "/queue/typing",
                    TypingEvent.builder()
                            .idConversacion(request.getIdConversacion())
                            .idUsuario(userId)
                            .escribiendo(request.isEscribiendo())
                            .build());
        }
    }

    @MessageMapping("/chat.online")
    public void online(Principal principal) {
        UUID userId = idUsuario(principal);
        presenciaService.marcarOnline(userId);
        difundirPresencia(userId);
    }

    @MessageMapping("/chat.offline")
    public void offline(Principal principal) {
        UUID userId = idUsuario(principal);
        presenciaService.marcarOffline(userId);
        difundirPresencia(userId);
    }

    // ---------------------------------------------------------------- helpers

    private void difundirPresencia(UUID userId) {
        PresenciaResponse presencia = presenciaService.obtenerPresencia(userId);
        // Los interesados se suscriben a /topic/presence/{userId}.
        messagingTemplate.convertAndSend("/topic/presence/" + userId, presencia);
    }

    private UUID otroParticipante(UUID idConversacion, UUID userId) {
        ConversacionDocument conv = conversacionService.cargarParticipando(idConversacion, userId);
        return userId.equals(conv.getIdCliente()) ? conv.getIdTecnico() : conv.getIdCliente();
    }

    private UUID idUsuario(Principal principal) {
        Authentication auth = (Authentication) principal;
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        return user.getId();
    }

    private void conContexto(Principal principal, Runnable accion) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication((Authentication) principal);
        SecurityContextHolder.setContext(ctx);
        try {
            accion.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
