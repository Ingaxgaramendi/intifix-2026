package com.intifix.modules.services.entity;

import com.intifix.modules.services.entity.enums.TipoArchivo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidencias_servicio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaServicio {

    @Id
    @Column(name = "id_evidencia") // Corregido por completo el ID de la tabla
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(name = "url_archivo", nullable = false, columnDefinition = "TEXT")
    private String urlArchivo;

    // MAPEO NATIVO CONTRA TU ENUM EXTERNO
    @Column(nullable = false, columnDefinition = "tipo_archivo")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoArchivo tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "subido_por", nullable = false)
    private UUID subidoPor;

    @Column(name = "fecha_subida", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime fechaSubida = OffsetDateTime.now();
}
