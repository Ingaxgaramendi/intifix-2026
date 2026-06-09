package com.intifix.modules.logging.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "logs_api")
public class ApiLogDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    private String method;

    @Indexed
    private String path;

    private int statusCode;

    private long durationMs;

    private String ip;

    private String userAgent;

    @Indexed
    private Instant createdAt = Instant.now();

    public String getId() {
        return id;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
