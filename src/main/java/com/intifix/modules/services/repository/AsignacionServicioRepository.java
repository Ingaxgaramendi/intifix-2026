package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.enums.EstadoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AsignacionServicio entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Repository
public interface AsignacionServicioRepository extends JpaRepository<AsignacionServicio, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    boolean existsByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Optional<AsignacionServicio> findByIdServicio(UUID idServicio);

    List<AsignacionServicio> findByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Page<AsignacionServicio> findByIdUsuarioTecnico(UUID idUsuarioTecnico, Pageable pageable);

    List<AsignacionServicio> findByIdCotizacion(UUID idCotizacion);

    List<AsignacionServicio> findByEstadoServicio(EstadoServicio estadoServicio);

    List<AsignacionServicio> findByIdUsuarioTecnicoAndEstadoServicio(UUID idUsuarioTecnico, EstadoServicio estadoServicio);

    @Query("SELECT a FROM AsignacionServicio a WHERE a.fechaAsignacion BETWEEN :inicio AND :fin")
    List<AsignacionServicio> findByFechaAsignacionBetween(
        @Param("inicio") ZonedDateTime inicio,
        @Param("fin") ZonedDateTime fin
    );

    @Query("SELECT a FROM AsignacionServicio a WHERE a.estadoServicio = :estado AND a.fechaFinReal IS NULL")
    List<AsignacionServicio> findActiveByEstado(@Param("estado") EstadoServicio estado);

    @Query("SELECT COUNT(a) FROM AsignacionServicio a WHERE a.idUsuarioTecnico = :idUsuarioTecnico")
    long countByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT COUNT(a) FROM AsignacionServicio a WHERE a.estadoServicio = :estado")
    long countByEstadoServicio(@Param("estado") EstadoServicio estado);

    @Query("SELECT a FROM AsignacionServicio a WHERE a.idUsuarioTecnico = :idUsuarioTecnico ORDER BY a.fechaAsignacion DESC")
    List<AsignacionServicio> findByIdUsuarioTecnicoOrderByFechaAsignacionDesc(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);
}
