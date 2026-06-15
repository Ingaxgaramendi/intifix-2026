package com.intifix.modules.audit.listener;

import com.intifix.modules.audit.entity.GeoAction;
import com.intifix.modules.audit.entity.GeoLogDocument;
import com.intifix.modules.audit.event.LocationUpdatedEvent;
import com.intifix.modules.audit.service.GeoLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Registra los eventos de geolocalización en {@code geo_logs}. La búsqueda por
 * cercanía y el cálculo de distancia se capturan vía AOP ({@code @Auditable}) en
 * los servicios de geo; aquí se registra la actualización de ubicación.
 */
@Component
@RequiredArgsConstructor
public class GeoAuditListener {

    private final GeoLogService geoLogService;

    @EventListener
    public void onLocationUpdated(LocationUpdatedEvent event) {
        GeoLogDocument log = GeoLogDocument.builder()
                .id(UUID.randomUUID())
                .userId(event.userId())
                .lat(event.lat())
                .lng(event.lng())
                .action(GeoAction.LOCATION_UPDATED)
                .build();
        geoLogService.registrar(log);
    }
}
