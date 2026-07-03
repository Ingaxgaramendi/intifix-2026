package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.enums.TipoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Servicio entity.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, UUID> {

    boolean existsByIdCliente(UUID idCliente);

    boolean existsByIdUbicacion(UUID idUbicacion);

    Page<Servicio> findByIdCliente(UUID idCliente, Pageable pageable);

    Page<Servicio> findByIdUbicacion(UUID idUbicacion, Pageable pageable);

    Page<Servicio> findByEstado(EstadoServicio estado, Pageable pageable);

    Page<Servicio> findByEstadoIn(Collection<EstadoServicio> estados, Pageable pageable);

    Page<Servicio> findByEstadoInAndTipoSolicitud(Collection<EstadoServicio> estados, TipoSolicitud tipoSolicitud, Pageable pageable);

    Page<Servicio> findByIdTecnicoDirectoAndEstadoIn(UUID idTecnicoDirecto, Collection<EstadoServicio> estados, Pageable pageable);

    List<Servicio> findByIdClienteAndEstado(UUID idCliente, EstadoServicio estado);

    List<Servicio> findByEstadoAndFechaCreacionAfter(EstadoServicio estado, ZonedDateTime fecha);

    @Query("SELECT s FROM Servicio s WHERE s.estado = :estado AND s.fechaProgramada BETWEEN :inicio AND :fin")
    List<Servicio> findByEstadoAndFechaProgramadaBetween(
        @Param("estado") EstadoServicio estado,
        @Param("inicio") ZonedDateTime inicio,
        @Param("fin") ZonedDateTime fin
    );

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.idCliente = :idCliente")
    long countByIdCliente(@Param("idCliente") UUID idCliente);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.estado = :estado")
    long countByEstado(@Param("estado") EstadoServicio estado);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.idUbicacion = :idUbicacion")
    long countByIdUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT s FROM Servicio s WHERE s.titulo ILIKE %:titulo%")
    Page<Servicio> buscarPorTitulo(@Param("titulo") String titulo, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.descripcion ILIKE %:descripcion%")
    Page<Servicio> buscarPorDescripcion(@Param("descripcion") String descripcion, Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.estado = :estado ORDER BY s.fechaCreacion DESC")
    List<Servicio> findByEstadoOrderByFechaCreacionDesc(@Param("estado") EstadoServicio estado);

    @Query("SELECT s FROM Servicio s WHERE s.idCliente = :idCliente ORDER BY s.fechaCreacion DESC")
    List<Servicio> findByIdClienteOrderByFechaCreacionDesc(@Param("idCliente") UUID idCliente);
}
