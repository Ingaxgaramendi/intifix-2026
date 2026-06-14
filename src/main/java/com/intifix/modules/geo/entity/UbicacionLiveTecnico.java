package com.intifix.modules.geo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Ubicación en tiempo real del técnico (GPS de su dispositivo). Es estado
 * dinámico de alta frecuencia: vive SOLO en MongoDB, nunca en PostgreSQL.
 *
 * <p>Decisión de modelado: el UUID del técnico ES el {@code _id} del documento
 * (un único documento de ubicación live por técnico). Esto da upsert atómico en
 * cada ping y elimina por construcción los duplicados; por eso "id" y
 * "tecnicoUuid" colapsan en un solo campo.</p>
 *
 * <p>Dos índices: {@code 2dsphere} sobre las coordenadas (búsqueda por cercanía
 * en el motor de Mongo) y un {@code TTL} sobre la última actualización (si el
 * técnico deja de emitir, su documento caduca y la búsqueda cae a su ubicación
 * pública de PostgreSQL).</p>
 */
@Document(collection = "ubicaciones_live_tecnicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionLiveTecnico {

    @Id
    private UUID tecnicoUuid;

    // GeoJSON Point: { "type": "Point", "coordinates": [longitud, latitud] }
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint coordenadas;

    @Builder.Default
    private boolean conectado = true;

    // TTL: el documento caduca 30 min después de la última actualización.
    @Indexed(name = "ttl_ultima_actualizacion", expireAfter = "30m")
    private Instant ultimaActualizacion;

    // Precisión del GPS en metros (accuracy del dispositivo).
    private Double precision;

    private OrigenUbicacion origen;

    @Builder.Default
    private EstadoLive estado = EstadoLive.ACTIVO;
}
