package com.intifix.modules.geo;

import com.intifix.modules.geo.service.GeoDistanceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoDistanceServiceTest {

    private final GeoDistanceService service = new GeoDistanceService();

    @Test
    void haversine_returnsPositiveDistance() {
        double km = service.haversineKm(19.4326, -99.1332, 19.4426, -99.1332);
        assertTrue(km > 0 && km < 5);
    }

    @Test
    void fallbackDistance_isConfigured() {
        assertTrue(service.fallbackDistanceKm() > 0);
    }
}
