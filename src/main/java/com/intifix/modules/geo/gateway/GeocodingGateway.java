package com.intifix.modules.geo.gateway;

import java.util.Optional;

/**
 * Puerto de geocodificación (dirección ↔ coordenadas). El proveedor concreto
 * (Nominatim, Google Maps...) se elige por configuración sin tocar los
 * servicios: patrón Gateway + selección por propiedad.
 */
public interface GeocodingGateway {

    /** Dirección de texto → coordenadas. */
    Optional<Coordenada> geocodificar(String direccion);

    /** Coordenadas → dirección formateada (geocodificación inversa). */
    Optional<String> geocodificarInverso(double latitud, double longitud);

    record Coordenada(double latitud, double longitud, String direccionFormateada) {}
}
