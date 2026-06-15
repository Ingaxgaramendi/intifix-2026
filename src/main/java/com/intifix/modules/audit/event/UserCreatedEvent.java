package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica cuando se registra un usuario. Lo consume el módulo audit para
 * persistir un evento de negocio; el productor no conoce a audit.
 */
public record UserCreatedEvent(
        UUID userId,
        String email,
        String nombre,
        String rol
) {}
