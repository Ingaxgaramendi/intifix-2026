package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica cuando un técnico actualiza su ubicación. Lo consume audit para
 * registrar el evento en geo_logs.
 */
public record LocationUpdatedEvent(
        UUID userId,
        double lat,
        double lng
) {}
