package com.intifix.modules.chat.document;

import com.intifix.modules.chat.entity.MessageStatus;
import com.intifix.modules.chat.entity.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "mensajes")
public class MensajeDocument {

    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String conversacionId;

    @Indexed
    private String remitenteId;

    private String contenido;

    private MessageType tipo;

    private MessageStatus estado;

    private String replyToMessageId;

    private String mediaUrl;

    private java.util.Map < String, Object > mediaMetadata;

    private Double latitud;

    private Double longitud;

    private String ubicacionNombre;

    @Indexed
    private Instant createdAt = Instant.now();

    private Instant editedAt;

    private Instant deletedAt;

    private Integer attachmentCount = 0;

    private java.util.Map < String, java.util.List < String >> emojiReactions;

    public String getId() {
        return id;
    }

    public String getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(String conversacionId) {
        this.conversacionId = conversacionId;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public MessageType getTipo() {
        return tipo;
    }

    public void setTipo(MessageType tipo) {
        this.tipo = tipo;
    }

    public MessageStatus getEstado() {
        return estado;
    }

    public void setEstado(MessageStatus estado) {
        this.estado = estado;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public java.util.Map < String, Object > getMediaMetadata() {
        return mediaMetadata;
    }

    public void setMediaMetadata(java.util.Map < String, Object > mediaMetadata) {
        this.mediaMetadata = mediaMetadata;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Instant editedAt) {
        this.editedAt = editedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public java.util.Map < String, java.util.List < String >> getEmojiReactions() {
        return emojiReactions;
    }

    public void setEmojiReactions(java.util.Map < String, java.util.List < String >> emojiReactions) {
        this.emojiReactions = emojiReactions;
    }
}
