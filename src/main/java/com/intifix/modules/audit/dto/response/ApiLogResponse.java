package com.intifix.modules.audit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ApiLogResponse(
        UUID requestId,
        String method,
        String path,
        int status,
        long durationMs,
        UUID userId,
        String ipAddress,
        Instant timestamp
) {}
