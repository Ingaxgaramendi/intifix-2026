package com.intifix.modules.geo.util;

/**
 * Utilidades geográficas para el camino que NO usa el motor de Mongo:
 * el cálculo puntual de distancia (HU-41) y el bounding box + distancia exacta
 * del fallback sobre ubicaciones públicas en PostgreSQL.
 *
 * <p>La búsqueda live por cercanía NO usa esta clase: la resuelve MongoDB con
 * su índice 2dsphere.</p>
 */
public final class GeoUtils {

    private static final double RADIO_TIERRA_KM = 6371.0088;
    private static final double KM_POR_GRADO_LAT = 111.0;

    private GeoUtils() {
    }

    /** Distancia del gran círculo (Haversine) entre dos puntos, en kilómetros. */
    public static double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return RADIO_TIERRA_KM * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    /** Delta de latitud (grados) que cubre un radio dado en km. */
    public static double deltaLat(double radioKm) {
        return radioKm / KM_POR_GRADO_LAT;
    }

    /** Delta de longitud (grados) para un radio dado a una latitud dada. */
    public static double deltaLng(double radioKm, double latitud) {
        double cos = Math.cos(Math.toRadians(latitud));
        if (Math.abs(cos) < 1e-6) {
            return 180.0; // cerca de los polos: abre todo el rango de longitud
        }
        return radioKm / (KM_POR_GRADO_LAT * cos);
    }

    public static boolean coordenadasValidas(Double lat, Double lng) {
        return lat != null && lng != null
                && lat >= -90 && lat <= 90
                && lng >= -180 && lng <= 180;
    }
}
