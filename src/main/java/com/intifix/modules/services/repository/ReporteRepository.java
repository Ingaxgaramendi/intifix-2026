package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Reporte;
import com.intifix.modules.services.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Reporte entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    boolean existsByIdReportante(UUID idReportante);

    boolean existsByIdReportado(UUID idReportado);

    Page<Reporte> findByIdServicio(UUID idServicio, Pageable pageable);

    Page<Reporte> findByIdReportante(UUID idReportante, Pageable pageable);

    Page<Reporte> findByIdReportado(UUID idReportado, Pageable pageable);

    Page<Reporte> findByEstado(EstadoReporte estado, Pageable pageable);

    Page<Reporte> findByTipoReporte(String tipoReporte, Pageable pageable);

    List<Reporte> findByPrioridad(String prioridad);

    List<Reporte> findByIdReportanteAndEstado(UUID idReportante, EstadoReporte estado);

    List<Reporte> findByIdReportadoAndEstado(UUID idReportado, EstadoReporte estado);

    @Query("SELECT r FROM Reporte r WHERE r.estado = 'PENDIENTE' ORDER BY r.fechaReporte ASC")
    Page<Reporte> findPendientesOrderByFechaReporteAsc(Pageable pageable);

    @Query("SELECT r FROM Reporte r WHERE r.estado = 'PENDIENTE' AND r.prioridad = 'ALTA' ORDER BY r.fechaReporte ASC")
    Page<Reporte> findPendientesAltaPrioridad(Pageable pageable);

    @Query("SELECT r FROM Reporte r WHERE r.fechaReporte BETWEEN :inicio AND :fin")
    List<Reporte> findByFechaReporteBetween(
        @Param("inicio") ZonedDateTime inicio,
        @Param("fin") ZonedDateTime fin
    );

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.estado = :estado")
    long countByEstado(@Param("estado") EstadoReporte estado);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.idReportante = :idReportante")
    long countByIdReportante(@Param("idReportante") UUID idReportante);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.idReportado = :idReportado")
    long countByIdReportado(@Param("idReportado") UUID idReportado);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.tipoReporte = :tipoReporte")
    long countByTipoReporte(@Param("tipoReporte") String tipoReporte);

    @Query("SELECT r FROM Reporte r WHERE r.idReportante = :idReportante ORDER BY r.fechaReporte DESC")
    List<Reporte> findByIdReportanteOrderByFechaReporteDesc(@Param("idReportante") UUID idReportante);

    @Query("SELECT r FROM Reporte r WHERE r.idReportado = :idReportado ORDER BY r.fechaReporte DESC")
    List<Reporte> findByIdReportadoOrderByFechaReporteDesc(@Param("idReportado") UUID idReportado);
}
