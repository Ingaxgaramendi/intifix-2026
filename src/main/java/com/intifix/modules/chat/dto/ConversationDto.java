package com.intifix.modules.chat.dto;

import java.time.Instant;
import java.util.List;

public record ConversationDto(String id, String servicioId, List<String> participantes, Instant createdAt) {
}
