package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Crea una conversación cliente↔técnico para un servicio. El cliente se toma del
 * usuario autenticado; el servicio determina al técnico (asignación).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearConversacionRequest {

    @NotNull(message = "El idServicio es obligatorio")
    private UUID idServicio;
}
