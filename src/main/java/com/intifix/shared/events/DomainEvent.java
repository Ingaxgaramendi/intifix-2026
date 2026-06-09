package com.intifix.shared.events;

import java.time.Instant;
import java.util.UUID;

public record DomainEvent(
        UUID eventId,
        String type,
        String aggregateType,
        UUID aggregateId,
        UUID actorId,
        Instant occurredAt,
        Object payload
) {
    public static DomainEvent of(String type, String aggregateType, UUID aggregateId, UUID actorId, Object payload) {
        return new DomainEvent(UUID.randomUUID(), type, aggregateType, aggregateId, actorId, Instant.now(), payload);
    }
}
