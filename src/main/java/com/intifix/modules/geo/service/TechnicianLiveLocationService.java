package com.intifix.modules.geo.service;

import com.intifix.modules.geo.document.TechnicianLiveLocationDocument;
import com.intifix.modules.geo.dto.LiveLocationDto;
import com.intifix.modules.geo.repository.TechnicianLiveLocationRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TechnicianLiveLocationService {

    private final TechnicianLiveLocationRepository repository;
    private final MongoTemplate mongoTemplate;
    private final GeoDistanceService geoDistanceService;

    public TechnicianLiveLocationService(
            TechnicianLiveLocationRepository repository,
            MongoTemplate mongoTemplate,
            GeoDistanceService geoDistanceService
    ) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.geoDistanceService = geoDistanceService;
    }

    public LiveLocationDto upsert(UUID tecnicoId, double lat, double lng) {
        TechnicianLiveLocationDocument doc = repository.findById(tecnicoId.toString())
                .orElse(new TechnicianLiveLocationDocument(tecnicoId.toString(), lat, lng));
        doc.setLatitud(lat);
        doc.setLongitud(lng);
        doc.touch();
        repository.save(doc);
        return new LiveLocationDto(tecnicoId, lat, lng, doc.getUpdatedAt());
    }

    public Optional<LiveLocationDto> find(UUID tecnicoId) {
        return repository.findById(tecnicoId.toString())
                .map(d -> new LiveLocationDto(tecnicoId, d.getLatitud(), d.getLongitud(), d.getUpdatedAt()));
    }

    public List<LiveLocationDto> searchNearby(double lat, double lng, double radiusKm) {
        try {
            NearQuery near = NearQuery.near(new Point(lng, lat), Metrics.KILOMETERS)
                    .maxDistance(new Distance(radiusKm, Metrics.KILOMETERS))
                    .spherical(true);
            return mongoTemplate.geoNear(near, TechnicianLiveLocationDocument.class).getContent().stream()
                    .map(r -> {
                        TechnicianLiveLocationDocument d = r.getContent();
                        return new LiveLocationDto(
                                UUID.fromString(d.getTecnicoId()),
                                d.getLatitud(),
                                d.getLongitud(),
                                d.getUpdatedAt()
                        );
                    })
                    .toList();
        } catch (Exception ex) {
            return repository.findAll().stream()
                    .filter(d -> geoDistanceService.haversineKm(lat, lng, d.getLatitud(), d.getLongitud()) <= radiusKm)
                    .map(d -> new LiveLocationDto(UUID.fromString(d.getTecnicoId()), d.getLatitud(), d.getLongitud(), d.getUpdatedAt()))
                    .toList();
        }
    }

    public void delete(UUID tecnicoId) {
        repository.deleteById(tecnicoId.toString());
    }
}
