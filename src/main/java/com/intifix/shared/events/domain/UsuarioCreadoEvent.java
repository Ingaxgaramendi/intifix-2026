package com.intifix.shared.events.domain;

import com.intifix.shared.dto.UserEventDTO;

public record UsuarioCreadoEvent(
    UserEventDTO usuario,
    long timestamp
) {
    public UsuarioCreadoEvent(UserEventDTO usuario) {
        this(usuario, System.currentTimeMillis());
    }
}
