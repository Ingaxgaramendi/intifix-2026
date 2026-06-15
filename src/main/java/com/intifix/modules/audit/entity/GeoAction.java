package com.intifix.modules.audit.entity;

/**
 * Acción registrada en {@code geo_logs}: actualizaciones de ubicación y
 * consultas geoespaciales.
 */
public enum GeoAction {
    LOCATION_UPDATED,
    NEARBY_SEARCH,
    DISTANCE_CALC
}
