package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.PerfilTecnico;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilTecnicoRepository extends JpaRepository<PerfilTecnico, UUID>, JpaSpecificationExecutor<PerfilTecnico> {

    Optional<PerfilTecnico> findByIdUsuario(UUID idUsuario);

    Optional<PerfilTecnico> findByDniRuc(String dniRuc);

    boolean existsByDniRuc(String dniRuc);

    boolean existsByIdUsuario(UUID idUsuario);

    Page<PerfilTecnico> findByDisponibilidad(DisponibilidadTecnico disponibilidad, Pageable pageable);

    Page<PerfilTecnico> findByEstadoAprobacion(EstadoAprobacionTecnico estadoAprobacion, Pageable pageable);

    @Query("SELECT pt FROM PerfilTecnico pt WHERE pt.disponibilidad = :disponibilidad AND pt.estadoAprobacion = :estadoAprobacion")
    Page<PerfilTecnico> buscarTecnicosDisponibles(
            @Param("disponibilidad") DisponibilidadTecnico disponibilidad,
            @Param("estadoAprobacion") EstadoAprobacionTecnico estadoAprobacion,
            Pageable pageable);

    @Query("SELECT DISTINCT pt FROM PerfilTecnico pt " +
           "JOIN TecnicoEspecialidad te ON pt.idUsuario = te.idUsuarioTecnico " +
           "WHERE te.idEspecialidad = :idEspecialidad")
    Page<PerfilTecnico> buscarPorEspecialidad(@Param("idEspecialidad") UUID idEspecialidad, Pageable pageable);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.estadoAprobacion = :estadoAprobacion")
    long contarTecnicosPorEstado(@Param("estadoAprobacion") EstadoAprobacionTecnico estadoAprobacion);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.disponibilidad = :disponibilidad")
    long contarTecnicosPorDisponibilidad(@Param("disponibilidad") DisponibilidadTecnico disponibilidad);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt")
    long contarTotalTecnicos();

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.estadoAprobacion = 'APROBADO'")
    long contarTecnicosAprobados();

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.estadoAprobacion = 'APROBADO' AND pt.disponibilidad = 'DISPONIBLE'")
    long contarTecnicosActivos();

    List<PerfilTecnico> findByIdUbicacion(UUID idUbicacion);

    @Query("SELECT pt FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.disponibilidad = 'DISPONIBLE'")
    List<PerfilTecnico> buscarTecnicosDisponiblesPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT pt FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.estadoAprobacion = 'APROBADO'")
    List<PerfilTecnico> buscarTecnicosAprobadosPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT pt FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.disponibilidad = 'DISPONIBLE' AND pt.estadoAprobacion = 'APROBADO'")
    List<PerfilTecnico> buscarTecnicosDisponiblesYAprobadosPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion")
    long contarTecnicosPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.disponibilidad = 'DISPONIBLE'")
    long contarTecnicosDisponiblesPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.estadoAprobacion = 'APROBADO'")
    long contarTecnicosAprobadosPorUbicacion(@Param("idUbicacion") UUID idUbicacion);

    @Query("SELECT COUNT(pt) FROM PerfilTecnico pt WHERE pt.idUbicacion = :idUbicacion AND pt.disponibilidad = 'DISPONIBLE' AND pt.estadoAprobacion = 'APROBADO'")
    long contarTecnicosDisponiblesYAprobadosPorUbicacion(@Param("idUbicacion") UUID idUbicacion);
}
