package com.intifix.modules.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Actividad de la mensajería en tiempo real en {@code websocket_logs}:
 * conexión, desconexión, mensaje enviado y mensaje leído.
 */
@Document(collection = "websocket_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_ws_conv_ts", def = "{'conversationId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_ws_user_ts", def = "{'userId': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketLogDocument {

    @Id
    private UUID id;

    private UUID conversationId;

    private UUID userId;

    private WebSocketAction action;

    @CreatedDate
    private Instant timestamp;
}
