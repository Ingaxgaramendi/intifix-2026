package com.intifix.modules.logging.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "logs_errores")
public class ErrorLogDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    private String message;

    private String stackTrace;

    private String source;

    @Indexed
    private Instant createdAt = Instant.now();

    public ErrorLogDocument() {
    }

    public ErrorLogDocument(String message, String stackTrace, String source) {
        this.message = message;
        this.stackTrace = stackTrace;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
