package com.intifix.modules.audit.dto.response;

import com.intifix.modules.audit.entity.SecurityReason;

import java.time.Instant;
import java.util.UUID;

public record SecurityEventResponse(
        UUID eventId,
        UUID userId,
        String email,
        String ipAddress,
        SecurityReason reason,
        String country,
        Instant timestamp
) {}
