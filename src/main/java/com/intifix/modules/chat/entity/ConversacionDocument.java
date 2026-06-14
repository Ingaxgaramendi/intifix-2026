package com.intifix.modules.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Conversación 1:1 entre un cliente y un técnico, anclada a un servicio.
 *
 * <p>Modelado para inbox eficiente: el último mensaje se denormaliza aquí
 * (evita un lookup por cada fila del inbox) y los contadores de no leídos se
 * mantienen por participante. Un único documento por servicio garantiza que no
 * existan conversaciones duplicadas (índice único en idServicio).</p>
 *
 * <p>La presencia (online/escribiendo/última conexión) NO vive aquí: es estado
 * volátil y se gestiona en Redis.</p>
 */
@Document(collection = "conversaciones")
@CompoundIndexes({
    // Inbox del cliente y del técnico: ordenado por actividad reciente.
    @CompoundIndex(name = "idx_inbox_cliente", def = "{'idCliente': 1, 'actualizadoEn': -1}"),
    @CompoundIndex(name = "idx_inbox_tecnico", def = "{'idTecnico': 1, 'actualizadoEn': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionDocument {

    @Id
    private UUID id;

    // Una sola conversación por servicio (cliente <-> técnico). Evita duplicados.
    @Indexed(unique = true, name = "uk_conversacion_servicio")
    private UUID idServicio;

    private UUID idCliente;

    private UUID idTecnico;

    @Builder.Default
    private EstadoConversacion estado = EstadoConversacion.ACTIVA;

    // Quién bloqueó la conversación (null si no está bloqueada).
    private UUID bloqueadaPor;

    // Denormalización para el inbox: se actualiza con cada mensaje.
    private UltimoMensaje ultimoMensaje;

    @Builder.Default
    private long noLeidosCliente = 0;

    @Builder.Default
    private long noLeidosTecnico = 0;

    @CreatedDate
    private Instant creadoEn;

    @LastModifiedDate
    private Instant actualizadoEn;

    @Version
    private Long version;

    /**
     * Resumen del último mensaje, denormalizado para listar el inbox sin
     * consultar la colección de mensajes.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UltimoMensaje {
        private UUID idMensaje;
        private UUID idEmisor;
        private TipoMensaje tipo;
        private String preview;
        private Instant fecha;
    }
}
