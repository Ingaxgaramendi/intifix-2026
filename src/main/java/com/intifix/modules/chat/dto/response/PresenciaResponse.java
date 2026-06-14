package com.intifix.modules.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenciaResponse {
    private UUID idUsuario;
    private boolean online;
    private Instant ultimaConexion;
}
