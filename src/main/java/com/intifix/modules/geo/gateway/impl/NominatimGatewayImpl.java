package com.intifix.modules.geo.gateway.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intifix.modules.geo.gateway.GeocodingGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Geocodificación con Nominatim (OpenStreetMap). Proveedor por defecto.
 * Activo salvo que se configure {@code geo.geocoding.provider=google}.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "geo.geocoding.provider", havingValue = "nominatim", matchIfMissing = true)
@RequiredArgsConstructor
public class NominatimGatewayImpl implements GeocodingGateway {

    private final RestClient nominatimRestClient;

    @Override
    public Optional<Coordenada> geocodificar(String direccion) {
        try {
            NominatimResult[] resultados = nominatimRestClient.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("q", direccion)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(NominatimResult[].class);

            if (resultados == null || resultados.length == 0) {
                return Optional.empty();
            }
            NominatimResult r = resultados[0];
            return Optional.of(new Coordenada(
                    Double.parseDouble(r.lat()),
                    Double.parseDouble(r.lon()),
                    r.displayName()));
        } catch (Exception e) {
            log.error("Error geocodificando '{}' con Nominatim", direccion, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> geocodificarInverso(double latitud, double longitud) {
        try {
            NominatimResult r = nominatimRestClient.get()
                    .uri(uri -> uri.path("/reverse")
                            .queryParam("lat", latitud)
                            .queryParam("lon", longitud)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(NominatimResult.class);
            return Optional.ofNullable(r).map(NominatimResult::displayName);
        } catch (Exception e) {
            log.error("Error en geocodificación inversa ({}, {}) con Nominatim", latitud, longitud, e);
            return Optional.empty();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NominatimResult(String lat, String lon, @JsonProperty("display_name") String displayName) {}
}
