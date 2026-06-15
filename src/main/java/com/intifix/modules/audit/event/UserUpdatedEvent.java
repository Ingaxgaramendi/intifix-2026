package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica al actualizarse el perfil de un usuario. Transporta el snapshot
 * anterior y nuevo para que audit guarde el diff.
 */
public record UserUpdatedEvent(
        UUID userId,
        Object oldValue,
        Object newValue
) {}
