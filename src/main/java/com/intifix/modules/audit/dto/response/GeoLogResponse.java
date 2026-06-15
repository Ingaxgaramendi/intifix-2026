package com.intifix.modules.audit.dto.response;

import com.intifix.modules.audit.entity.GeoAction;

import java.time.Instant;
import java.util.UUID;

public record GeoLogResponse(
        UUID id,
        UUID userId,
        Double lat,
        Double lng,
        GeoAction action,
        Instant timestamp
) {}
