package com.intifix.modules.geo.gateway.impl;

import com.intifix.modules.geo.gateway.GeocodingGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación PREPARADA para Google Maps Geocoding API. Se activa con
 * {@code geo.geocoding.provider=google}. Cambiar de Nominatim a Google no
 * requiere tocar ningún servicio: ambos cumplen {@link GeocodingGateway}.
 *
 * <p>Pendiente: inyectar la API key y el cliente HTTP de Google. Mientras tanto
 * falla explícito para no devolver datos silenciosamente incorrectos.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "geo.geocoding.provider", havingValue = "google")
public class GoogleMapsGatewayImpl implements GeocodingGateway {

    @Override
    public Optional<Coordenada> geocodificar(String direccion) {
        throw new UnsupportedOperationException("Google Maps Geocoding aún no está configurado (falta API key)");
    }

    @Override
    public Optional<String> geocodificarInverso(double latitud, double longitud) {
        throw new UnsupportedOperationException("Google Maps Geocoding aún no está configurado (falta API key)");
    }
}
