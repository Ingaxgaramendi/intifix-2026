package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.HistorialServicio;
import com.intifix.modules.services.enums.EstadoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for HistorialServicio entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Repository
public interface HistorialServicioRepository extends JpaRepository<HistorialServicio, UUID> {

    List<HistorialServicio> findByIdServicio(UUID idServicio);

    List<HistorialServicio> findByCambiadoPor(UUID cambiadoPor);

    List<HistorialServicio> findByIdServicioOrderByFechaCambioDesc(UUID idServicio);

    List<HistorialServicio> findByEstadoNuevo(EstadoServicio estadoNuevo);

    @Query("SELECT h FROM HistorialServicio h WHERE h.idServicio = :idServicio AND h.fechaCambio BETWEEN :inicio AND :fin")
    List<HistorialServicio> findByIdServicioAndFechaCambioBetween(
        @Param("idServicio") UUID idServicio,
        @Param("inicio") ZonedDateTime inicio,
        @Param("fin") ZonedDateTime fin
    );

    @Query("SELECT COUNT(h) FROM HistorialServicio h WHERE h.idServicio = :idServicio")
    long countByIdServicio(@Param("idServicio") UUID idServicio);

    @Query("SELECT COUNT(h) FROM HistorialServicio h WHERE h.cambiadoPor = :cambiadoPor")
    long countByCambiadoPor(@Param("cambiadoPor") UUID cambiadoPor);

    @Query("SELECT h FROM HistorialServicio h WHERE h.idServicio = :idServicio ORDER BY h.fechaCambio DESC LIMIT :limite")
    List<HistorialServicio> findUltimosCambiosByIdServicio(
        @Param("idServicio") UUID idServicio,
        @Param("limite") int limite
    );
}
