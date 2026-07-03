package com.intifix.modules.ai.memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Mensaje de una conversación con el asistente de IA.
 *
 * <p>MongoDB se usa exclusivamente como capa de memoria conversacional; nunca
 * para lógica de negocio (esa vive en PostgreSQL).
 */
@Document(collection = "ai_conversaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {

    @Id
    private String id;

    /** Identificador lógico de la conversación (hilo). */
    @Indexed
    private String conversationId;

    /** "user" o "assistant". */
    private String role;

    private String content;

    @CreatedDate
    private Instant creadoEn;
}
