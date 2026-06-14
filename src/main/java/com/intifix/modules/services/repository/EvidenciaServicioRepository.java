package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.EvidenciaServicio;
import com.intifix.modules.services.enums.TipoArchivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for EvidenciaServicio entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Repository
public interface EvidenciaServicioRepository extends JpaRepository<EvidenciaServicio, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    List<EvidenciaServicio> findByIdServicio(UUID idServicio);

    List<EvidenciaServicio> findBySubidoPor(UUID subidoPor);

    List<EvidenciaServicio> findByTipoArchivo(TipoArchivo tipoArchivo);

    List<EvidenciaServicio> findByIdServicioAndSubidoPor(UUID idServicio, UUID subidoPor);

    List<EvidenciaServicio> findByIdServicioAndTipoArchivo(UUID idServicio, TipoArchivo tipoArchivo);

    @Query("SELECT COUNT(e) FROM EvidenciaServicio e WHERE e.idServicio = :idServicio")
    long countByIdServicio(@Param("idServicio") UUID idServicio);

    @Query("SELECT COUNT(e) FROM EvidenciaServicio e WHERE e.subidoPor = :subidoPor")
    long countBySubidoPor(@Param("subidoPor") UUID subidoPor);

    @Query("SELECT e FROM EvidenciaServicio e WHERE e.idServicio = :idServicio ORDER BY e.fechaSubida DESC")
    List<EvidenciaServicio> findByIdServicioOrderByFechaSubidaDesc(@Param("idServicio") UUID idServicio);

    @Query("SELECT e FROM EvidenciaServicio e WHERE e.subidoPor = :subidoPor ORDER BY e.fechaSubida DESC")
    List<EvidenciaServicio> findBySubidoPorOrderByFechaSubidaDesc(@Param("subidoPor") UUID subidoPor);
}
