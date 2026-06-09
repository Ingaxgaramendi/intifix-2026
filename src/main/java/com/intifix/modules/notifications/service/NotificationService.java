package com.intifix.modules.notifications.service;

import com.intifix.modules.notifications.document.NotificacionDocument;
import com.intifix.modules.notifications.dto.NotificationDto;
import com.intifix.modules.notifications.repository.NotificacionRepository;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import com.intifix.shared.events.DomainEvent;
import com.intifix.shared.exception.ApiException;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificacionRepository repository;

    public NotificationService(NotificacionRepository repository) {
        this.repository = repository;
    }

    public void notifyUser(UUID userId, String tipo, String titulo, String mensaje) {
        NotificacionDocument doc = new NotificacionDocument(userId.toString(), tipo, titulo, mensaje);
        doc.setEmailPayload(buildEmailPayload(userId, tipo, titulo, mensaje));
        repository.save(doc);
    }

    public PageResponse<NotificationDto> myNotifications(PageRequestDto page) {
        UUID userId = SecurityUtils.currentUserId();
        Page<NotificacionDocument> result = repository.findByUsuarioIdOrderByCreatedAtDesc(
                userId.toString(),
                PageRequest.of(page.page(), page.size())
        );
        return PageResponse.of(
                result.getContent().stream().map(this::toDto).toList(),
                page.page(),
                page.size(),
                result.getTotalElements()
        );
    }

    public void markRead(String notificationId) {
        UUID userId = SecurityUtils.currentUserId();
        NotificacionDocument doc = repository.findById(notificationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!doc.getUsuarioId().equals(userId.toString())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        doc.setLeida(true);
        repository.save(doc);
    }

    @Async
    @EventListener
    public void onDomainEvent(DomainEvent event) {
        // Hook for future fan-out (email, push, etc.)
    }

    private Map<String, Object> buildEmailPayload(UUID userId, String tipo, String titulo, String mensaje) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("toUserId", userId.toString());
        payload.put("subject", titulo);
        payload.put("body", mensaje);
        payload.put("template", "intifix-" + tipo.toLowerCase());
        return payload;
    }

    private NotificationDto toDto(NotificacionDocument doc) {
        return new NotificationDto(
                doc.getId(),
                doc.getTipo(),
                doc.getTitulo(),
                doc.getMensaje(),
                doc.isLeida(),
                doc.getCreatedAt()
        );
    }
}
