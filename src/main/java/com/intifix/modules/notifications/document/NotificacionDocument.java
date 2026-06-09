package com.intifix.modules.notifications.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document(collection = "notificaciones")
public class NotificacionDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String usuarioId;

    @Indexed
    private String tipo;

    private String titulo;

    private String mensaje;

    @Indexed
    private boolean leida;

    private String referenceId;

    private String referenceType;

    private String actionUrl;

    private String iconUrl;

    private String prioridad = "NORMAL";

    private java.util.Map < String, Object > emailPayload;

    @Indexed
    private Instant createdAt = Instant.now();

    private Instant leidaAt;

    private Instant expiresAt;

    public NotificacionDocument() {}

    public NotificacionDocument(String usuarioId, String tipo, String titulo, String mensaje) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = false;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public java.util.Map < String, Object > getEmailPayload() {
        return emailPayload;
    }

    public void setEmailPayload(java.util.Map < String, Object > emailPayload) {
        this.emailPayload = emailPayload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLeidaAt() {
        return leidaAt;
    }

    public void setLeidaAt(Instant leidaAt) {
        this.leidaAt = leidaAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
