package com.intifix.modules.chat.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateConversationRequest(@NotNull UUID servicioId) {
}
