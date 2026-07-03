package com.intifix.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Petición de chat hacia el motor de IA.
 */
@Data
public class ChatRequest {

    /** Identificador del hilo de conversación (memoria en MongoDB). */
    @NotBlank
    private String conversationId;

    /** UUID del cliente, opcional. Permite al modelo consultar su historial. */
    private String userId;

    /** Mensaje del usuario. */
    @NotBlank
    private String message;
}
