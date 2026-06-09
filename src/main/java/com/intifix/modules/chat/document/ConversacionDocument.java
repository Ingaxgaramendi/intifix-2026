package com.intifix.modules.chat.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "conversaciones")
public class ConversacionDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String servicioId;

    private java.util.List < ParticipantInfo > participantes;

    private String estado = "ACTIVA";

    @Indexed
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @Indexed
    private Instant lastMessageAt;

    private String lastMessagePreview;

    private Integer unreadCount = 0;

    public static class ParticipantInfo {
        public String usuarioId;
        public String nombre;
        public String rol;
        public Instant joinedAt;
        public Instant leftAt;

        public ParticipantInfo() {}

        public ParticipantInfo(String usuarioId, String nombre, String rol) {
            this.usuarioId = usuarioId;
            this.nombre = nombre;
            this.rol = rol;
            this.joinedAt = Instant.now();
        }
    }

    public ConversacionDocument() {}

    public ConversacionDocument(String servicioId, java.util.List < ParticipantInfo > participantes) {
        this.servicioId = servicioId;
        this.participantes = participantes;
    }

    public String getId() {
        return id;
    }

    public String getServicioId() {
        return servicioId;
    }

    public void setServicioId(String servicioId) {
        this.servicioId = servicioId;
    }

    public java.util.List < ParticipantInfo > getParticipantes() {
        return participantes;
    }

    public void setParticipantes(java.util.List < ParticipantInfo > participantes) {
        this.participantes = participantes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
