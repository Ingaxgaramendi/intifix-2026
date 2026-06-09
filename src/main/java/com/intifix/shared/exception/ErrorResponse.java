package com.intifix.shared.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        Object data,
        String message,
        Instant timestamp,
        Map<String, String> fieldErrors
) {
    public static ErrorResponse of(String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(false, null, message, Instant.now(), fieldErrors);
    }
}

