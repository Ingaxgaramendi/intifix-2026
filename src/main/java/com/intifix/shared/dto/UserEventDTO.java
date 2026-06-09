package com.intifix.shared.dto;

import java.util.UUID;

public record UserEventDTO(
    UUID idUsuario,
    String nombre,
    String correo,
    String rol
) {}
