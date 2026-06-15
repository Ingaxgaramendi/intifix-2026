package com.intifix.modules.audit.entity;

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
 * Evento de geolocalización en {@code geo_logs}: actualización de ubicación,
 * búsqueda por cercanía y cálculo de distancia.
 */
@Document(collection = "geo_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_geo_user_ts", def = "{'userId': 1, 'timestamp': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoLogDocument {

    @Id
    private UUID id;

    private UUID userId;

    private Double lat;

    private Double lng;

    private GeoAction action;

    @CreatedDate
    private Instant timestamp;
}
