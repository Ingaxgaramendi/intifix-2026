package com.intifix.modules.audit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ExceptionLogResponse(
        UUID id,
        String exceptionClass,
        String message,
        String stackTrace,
        String module,
        UUID userId,
        Instant timestamp
) {}
