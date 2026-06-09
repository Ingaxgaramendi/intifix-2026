package com.intifix.modules.logging.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "logs_seguridad")
public class SecurityLogDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String usuarioId;

    private String action;

    private String ip;

    private boolean success;

    @Indexed
    private Instant createdAt = Instant.now();

    public SecurityLogDocument() {
    }

    public SecurityLogDocument(String usuarioId, String action, String ip, boolean success) {
        this.usuarioId = usuarioId;
        this.action = action;
        this.ip = ip;
        this.success = success;
    }
}
