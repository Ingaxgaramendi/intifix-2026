package com.intifix.modules.geo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP para el proveedor de geocodificación por defecto (Nominatim).
 * Aislar el {@link RestClient} aquí permite cambiar de proveedor sin tocar los
 * servicios (ver {@code GeocodingGateway}).
 */
@Configuration
public class GeoConfig {

    @Bean
    public RestClient nominatimRestClient() {
        return RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                // Nominatim exige un User-Agent identificable en su política de uso.
                .defaultHeader("User-Agent", "Intifix/1.0 (soporte@intifix.com)")
                .build();
    }
}
