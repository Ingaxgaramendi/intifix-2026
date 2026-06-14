package com.intifix.modules.notifications.entity;

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
 * Notificación dirigida a un usuario, persistida en MongoDB. Es el registro
 * durable (bandeja de notificaciones); la entrega en tiempo real va por
 * WebSocket y el push externo (Firebase) se engancha en el listener.
 */
@Document(collection = "notificaciones")
@CompoundIndexes({
    // Bandeja del usuario, ordenada por más recientes.
    @CompoundIndex(name = "idx_inbox_destinatario", def = "{'idDestinatario': 1, 'creadoEn': -1}"),
    // Conteo / filtro de no leídas.
    @CompoundIndex(name = "idx_destinatario_leida", def = "{'idDestinatario': 1, 'leida': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDocument {

    @Id
    private UUID id;

    private UUID idDestinatario;

    private TipoNotificacion tipo;

    private String titulo;

    private String cuerpo;

    // Id del recurso relacionado (conversación, servicio, pago...) para deep-link.
    private UUID referenciaId;

    @Builder.Default
    private boolean leida = false;

    private Instant leidoEn;

    @CreatedDate
    private Instant creadoEn;
}
