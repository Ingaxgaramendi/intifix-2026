package com.intifix.modules.chat.service;

import com.intifix.modules.chat.document.ConversacionDocument;
import com.intifix.modules.chat.document.MensajeDocument;
import com.intifix.modules.chat.document.MensajeLeidoDocument;
import com.intifix.modules.chat.dto.ConversationDto;
import com.intifix.modules.chat.dto.CreateConversationRequest;
import com.intifix.modules.chat.dto.MessageDto;
import com.intifix.modules.chat.dto.SendMessageRequest;
import com.intifix.modules.chat.entity.MessageStatus;
import com.intifix.modules.chat.entity.MessageType;
import com.intifix.modules.chat.repository.ConversacionRepository;
import com.intifix.modules.chat.repository.MensajeLeidoRepository;
import com.intifix.modules.chat.repository.MensajeRepository;
import com.intifix.modules.notifications.service.NotificationService;
import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.repository.AsignacionServicioRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import com.intifix.shared.exception.ApiException;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeLeidoRepository mensajeLeidoRepository;
    private final ServicioRepository servicioRepository;
    private final AsignacionServicioRepository asignacionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public ChatService(
            ConversacionRepository conversacionRepository,
            MensajeRepository mensajeRepository,
            MensajeLeidoRepository mensajeLeidoRepository,
            ServicioRepository servicioRepository,
            AsignacionServicioRepository asignacionRepository,
            SimpMessagingTemplate messagingTemplate,
            NotificationService notificationService
    ) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.mensajeLeidoRepository = mensajeLeidoRepository;
        this.servicioRepository = servicioRepository;
        this.asignacionRepository = asignacionRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public ConversationDto createConversation(CreateConversationRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        Servicio servicio = servicioRepository.findById(req.servicioId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));
        ensureParticipant(servicio, userId);

        ConversacionDocument existing = conversacionRepository.findByServicioId(req.servicioId().toString()).orElse(null);
        if (existing != null) {
            return toConversationDto(existing);
        }

        List<String> participantes = new ArrayList<>();
        participantes.add(servicio.getIdCliente().toString());
        AsignacionServicio asignacion = asignacionRepository.findByServicioId(servicio.getIdServicio()).orElse(null);
        if (asignacion != null) {
            participantes.add(asignacion.getTecnicoId().toString());
        }

        ConversacionDocument doc = new ConversacionDocument(req.servicioId().toString(), participantes);
        conversacionRepository.save(doc);
        return toConversationDto(doc);
    }

    public MessageDto sendMessage(SendMessageRequest req) {
        UUID senderId = SecurityUtils.currentUserId();
        ConversacionDocument conversacion = conversacionRepository.findById(req.conversacionId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversation not found"));
        if (!conversacion.getParticipantes().contains(senderId.toString())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not a participant");
        }
        validatePayload(req);

        MensajeDocument msg = new MensajeDocument();
        msg.setConversacionId(req.conversacionId());
        msg.setRemitenteId(senderId.toString());
        msg.setTipo(req.tipo());
        msg.setContenido(req.contenido());
        msg.setMediaUrl(req.mediaUrl());
        msg.setLatitud(req.latitud());
        msg.setLongitud(req.longitud());
        msg.setReplyToMessageId(req.replyToMessageId());
        msg.setEstado(MessageStatus.SENT);
        mensajeRepository.save(msg);

        conversacion.touch();
        conversacionRepository.save(conversacion);

        MessageDto dto = toMessageDto(msg);
        messagingTemplate.convertAndSend("/topic/chat." + req.conversacionId(), dto);

        for (String participant : conversacion.getParticipantes()) {
            if (!participant.equals(senderId.toString())) {
                notificationService.notifyUser(
                        UUID.fromString(participant),
                        "MESSAGE",
                        "Nuevo mensaje",
                        "Tienes un mensaje en el chat del servicio"
                );
            }
        }
        return dto;
    }

    public PageResponse<MessageDto> listMessages(String conversacionId, PageRequestDto page) {
        UUID userId = SecurityUtils.currentUserId();
        ConversacionDocument conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversation not found"));
        if (!conversacion.getParticipantes().contains(userId.toString())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not a participant");
        }

        Page<MensajeDocument> result = mensajeRepository.findByConversacionIdOrderByCreatedAtDesc(
                conversacionId,
                PageRequest.of(page.page(), page.size())
        );
        return PageResponse.of(
                result.getContent().stream().map(this::toMessageDto).toList(),
                page.page(),
                page.size(),
                result.getTotalElements()
        );
    }

    public void markRead(String mensajeId) {
        UUID userId = SecurityUtils.currentUserId();
        MensajeDocument msg = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Message not found"));
        if (mensajeLeidoRepository.existsByMensajeIdAndUsuarioId(mensajeId, userId.toString())) {
            return;
        }
        mensajeLeidoRepository.save(new MensajeLeidoDocument(mensajeId, userId.toString()));
        if (!msg.getRemitenteId().equals(userId.toString())) {
            msg.setEstado(MessageStatus.READ);
            mensajeRepository.save(msg);
        }
    }

    private void validatePayload(SendMessageRequest req) {
        if (req.tipo() == MessageType.TEXT && (req.contenido() == null || req.contenido().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Text content required");
        }
        if ((req.tipo() == MessageType.IMAGE || req.tipo() == MessageType.FILE) && req.mediaUrl() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "mediaUrl required");
        }
        if (req.tipo() == MessageType.LOCATION && (req.latitud() == null || req.longitud() == null)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Coordinates required");
        }
    }

    private void ensureParticipant(Servicio servicio, UUID userId) {
        AsignacionServicio asignacion = asignacionRepository.findByServicioId(servicio.getIdServicio()).orElse(null);
        boolean allowed = userId.equals(servicio.getIdCliente())
                || (asignacion != null && userId.equals(asignacion.getTecnicoId()));
        if (!allowed) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed on this service");
        }
    }

    private ConversationDto toConversationDto(ConversacionDocument doc) {
        return new ConversationDto(doc.getId(), doc.getServicioId(), doc.getParticipantes(), doc.getCreatedAt());
    }

    private MessageDto toMessageDto(MensajeDocument msg) {
        return new MessageDto(
                msg.getId(),
                msg.getConversacionId(),
                msg.getRemitenteId(),
                msg.getContenido(),
                msg.getTipo(),
                msg.getEstado(),
                msg.getMediaUrl(),
                msg.getLatitud(),
                msg.getLongitud(),
                msg.getReplyToMessageId(),
                msg.getCreatedAt()
        );
    }
}
