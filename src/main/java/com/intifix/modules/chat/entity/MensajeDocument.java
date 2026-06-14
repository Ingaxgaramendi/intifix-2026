package com.intifix.modules.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Mensaje individual dentro de una conversación.
 *
 * <p>Patrón "one document per message" (no arrays embebidos en la conversación):
 * un hilo puede crecer sin límite y el documento de conversación tiene un tope
 * de 16MB en Mongo, por lo que embeber mensajes no escala. Los mensajes viven
 * en su propia colección, indexada por (idConversacion, creadoEn) para historial
 * paginado y scroll infinito.</p>
 *
 * <p>Los archivos adjuntos guardan SOLO la URL del objeto en almacenamiento
 * externo (Cloudinary/S3/MinIO); nunca el binario.</p>
 */
@Document(collection = "mensajes")
@CompoundIndexes({
    // Historial paginado / scroll infinito por conversación.
    @CompoundIndex(name = "idx_historial", def = "{'idConversacion': 1, 'creadoEn': -1}"),
    // Conteo de no leídos y marcado masivo como leído.
    @CompoundIndex(name = "idx_estado", def = "{'idConversacion': 1, 'estado': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDocument {

    @Id
    private UUID id;

    private UUID idConversacion;

    private UUID idEmisor;

    private String contenido;

    @Builder.Default
    private TipoMensaje tipo = TipoMensaje.TEXTO;

    @Builder.Default
    private EstadoMensaje estado = EstadoMensaje.ENVIADO;

    // Solo para tipos de archivo; null en mensajes de texto.
    private Adjunto adjunto;

    // Mensaje al que responde (hilo); null si no es respuesta.
    private UUID idMensajeRespondido;

    @Builder.Default
    private boolean editado = false;

    @Builder.Default
    private boolean eliminado = false;

    private Instant leidoEn;

    @CreatedDate
    private Instant creadoEn;

    @LastModifiedDate
    private Instant actualizadoEn;

    /**
     * Metadatos de un archivo adjunto. El binario vive en almacenamiento externo;
     * aquí solo se referencia su URL.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adjunto {
        private String url;
        private String nombreArchivo;
        private String tipoMime;
        private Long tamanoBytes;
    }
}
