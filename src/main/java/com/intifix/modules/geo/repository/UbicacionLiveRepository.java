package com.intifix.modules.geo.repository;

import com.intifix.modules.geo.entity.UbicacionLiveTecnico;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

/**
 * Repositorio de ubicaciones live. Las consultas {@code ...Near} devuelven
 * {@link GeoResults}: Spring Data construye internamente un {@code NearQuery}
 * ($nearSphere sobre el índice 2dsphere) y adjunta la distancia calculada por
 * MongoDB a cada resultado. No se recorre ninguna lista ni se hace Haversine
 * manual: el motor geoespacial hace el trabajo.
 */
@Repository
public interface UbicacionLiveRepository extends MongoRepository<UbicacionLiveTecnico, UUID> {

    GeoResults<UbicacionLiveTecnico> findByConectadoAndCoordenadasNear(
            boolean conectado, Point punto, Distance distancia);

    GeoResults<UbicacionLiveTecnico> findByConectadoAndTecnicoUuidInAndCoordenadasNear(
            boolean conectado, Collection<UUID> tecnicoUuids, Point punto, Distance distancia);
}
