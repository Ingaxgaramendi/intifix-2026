package com.intifix.modules.ai.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document(collection = "recomendaciones_ia")
public class RecomendacionIaDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String servicioId;

    @Indexed
    private String diagnosticoId;

    private Map<String, Object> payload;

    @Indexed
    private Instant createdAt = Instant.now();

    public RecomendacionIaDocument() {
    }

    public RecomendacionIaDocument(String servicioId, String diagnosticoId, Map<String, Object> payload) {
        this.servicioId = servicioId;
        this.diagnosticoId = diagnosticoId;
        this.payload = payload;
    }
}
