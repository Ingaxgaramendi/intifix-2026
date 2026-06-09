package com.intifix.modules.geo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ubicaciones_live_tecnicos")
public class TechnicianLiveLocationDocument {

    @Id
    private String tecnicoId;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private double latitud;

    private double longitud;

    @Indexed
    private Instant updatedAt = Instant.now();

    public TechnicianLiveLocationDocument() {
    }

    public TechnicianLiveLocationDocument(String tecnicoId, double latitud, double longitud) {
        this.tecnicoId = tecnicoId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.location = new GeoJsonPoint(longitud, latitud);
    }

    public String getTecnicoId() {
        return tecnicoId;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
        this.location = new GeoJsonPoint(longitud, latitud);
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
        this.location = new GeoJsonPoint(longitud, latitud);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
