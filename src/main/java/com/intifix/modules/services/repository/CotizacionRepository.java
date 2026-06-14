package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Cotizacion;
import com.intifix.modules.services.enums.EstadoCotizacion;
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
 * Repository for Cotizacion entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    boolean existsByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Page<Cotizacion> findByIdServicio(UUID idServicio, Pageable pageable);

    Page<Cotizacion> findByIdUsuarioTecnico(UUID idUsuarioTecnico, Pageable pageable);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    List<Cotizacion> findByIdServicioAndEstado(UUID idServicio, EstadoCotizacion estado);

    List<Cotizacion> findByIdUsuarioTecnicoAndEstado(UUID idUsuarioTecnico, EstadoCotizacion estado);

    @Query("SELECT c FROM Cotizacion c WHERE c.idServicio = :idServicio AND c.estado = 'PENDIENTE' AND c.fechaExpiracion > :ahora")
    Page<Cotizacion> findPendientesByServicio(@Param("idServicio") UUID idServicio, @Param("ahora") ZonedDateTime ahora, Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'PENDIENTE' AND c.fechaExpiracion < :ahora")
    List<Cotizacion> findExpired(@Param("ahora") ZonedDateTime ahora);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.idServicio = :idServicio")
    long countByIdServicio(@Param("idServicio") UUID idServicio);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.idUsuarioTecnico = :idUsuarioTecnico")
    long countByIdUsuarioTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.idServicio = :idServicio AND c.estado = :estado")
    long countByIdServicioAndEstado(@Param("idServicio") UUID idServicio, @Param("estado") EstadoCotizacion estado);

    @Query("SELECT c FROM Cotizacion c WHERE c.idServicio = :idServicio ORDER BY c.precio ASC")
    Page<Cotizacion> findByIdServicioOrderByPrecioAsc(@Param("idServicio") UUID idServicio, Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.idServicio = :idServicio ORDER BY c.precio DESC")
    Page<Cotizacion> findByIdServicioOrderByPrecioDesc(@Param("idServicio") UUID idServicio, Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.idServicio = :idServicio ORDER BY c.tiempoEstimado ASC")
    List<Cotizacion> findByIdServicioOrderByTiempoEstimadoAsc(@Param("idServicio") UUID idServicio);
}
