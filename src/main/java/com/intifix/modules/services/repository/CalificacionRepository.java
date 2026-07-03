package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Calificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Calificacion entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    boolean existsByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Optional<Calificacion> findByIdServicio(UUID idServicio);

    Page<Calificacion> findByIdUsuarioTecnico(UUID idUsuarioTecnico, Pageable pageable);

    Page<Calificacion> findByIdCliente(UUID idCliente, Pageable pageable);

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    Double averagePuntuacionByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT AVG(c.puntualidad) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    Double averagePuntualidadByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT AVG(c.profesionalismo) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    Double averageProfesionalismoByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT AVG(c.calidadTrabajo) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    Double averageCalidadTrabajoByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT AVG(c.comunicacion) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    Double averageComunicacionByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    long countByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.idCliente = :idCliente")
    long countByIdCliente(@Param("idCliente") UUID idCliente);

    @Query("SELECT (SELECT COUNT(c) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico AND c.recomendaria = true) * 100.0 / NULLIF((SELECT COUNT(c) FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico), 0)")
    Double porcentajeRecomendacionByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT c FROM Calificacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico ORDER BY c.fechaCalificacion DESC")
    List<Calificacion> findByIdUsuarioTecnicoOrderByFechaCalificacionDesc(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT c FROM Calificacion c WHERE c.fechaCalificacion BETWEEN :inicio AND :fin")
    List<Calificacion> findByFechaCalificacionBetween(
        @Param("inicio") ZonedDateTime inicio,
        @Param("fin") ZonedDateTime fin
    );
}
