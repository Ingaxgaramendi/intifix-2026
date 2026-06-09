package com.intifix.modules.chat.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "mensajes_leidos")
@CompoundIndex(name = "idx_mensaje_usuario", def = "{'mensajeId': 1, 'usuarioId': 1}", unique = true)
public class MensajeLeidoDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String mensajeId;

    @Indexed
    private String usuarioId;

    @Indexed
    private Instant readAt = Instant.now();

    public MensajeLeidoDocument() {
    }

    public MensajeLeidoDocument(String mensajeId, String usuarioId) {
        this.mensajeId = mensajeId;
        this.usuarioId = usuarioId;
    }
}
