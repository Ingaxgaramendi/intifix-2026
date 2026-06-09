package com.intifix.modules.notifications.dto;

import java.time.Instant;

public record NotificationDto(
        String id,
        String tipo,
        String titulo,
        String mensaje,
        boolean leida,
        Instant createdAt
) {
}
