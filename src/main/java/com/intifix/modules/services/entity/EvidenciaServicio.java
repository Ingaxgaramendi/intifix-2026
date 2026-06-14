package com.intifix.modules.services.entity;

import com.intifix.modules.services.enums.TipoArchivo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing evidence uploaded for a service.
 * 
 * This entity is owned by the services module and stores evidence data.
 * It references services by UUID only, without JPA relationships,
 * to maintain module independence for future microservices migration.
 * 
 * Evidence can include photos, videos, documents, and other files
 * that document the service work.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Entity
@Table(name = "evidencias_servicio", indexes = {
    @Index(name = "idx_evidencias_servicio", columnList = "id_servicio"),
    @Index(name = "idx_evidencias_tipo", columnList = "tipo"),
    @Index(name = "idx_evidencias_fecha_subida", columnList = "fecha_subida")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaServicio {

    @Id
    @Column(name = "id_evidencia", nullable = false, updatable = false)
    private UUID idEvidencia;

    @Column(name = "id_servicio", nullable = false)
    private UUID idServicio;

    @Column(name = "url_archivo", nullable = false, columnDefinition = "TEXT")
    private String urlArchivo;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    // La columna en BD se llama "tipo" (tipo PG: tipo_archivo)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false)
    private TipoArchivo tipoArchivo;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "subido_por", nullable = false)
    private UUID subidoPor;

    @Column(name = "fecha_subida", nullable = false)
    @Builder.Default
    private ZonedDateTime fechaSubida = ZonedDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadatos", columnDefinition = "JSONB")
    private String metadatos;

    @PrePersist
    protected void onCreate() {
        if (fechaSubida == null) {
            fechaSubida = ZonedDateTime.now();
        }
    }
}
