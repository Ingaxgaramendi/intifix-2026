package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.ExcepcionHorarioTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExcepcionHorarioTecnicoRepository extends JpaRepository<ExcepcionHorarioTecnico, UUID>, JpaSpecificationExecutor<ExcepcionHorarioTecnico> {

    List<ExcepcionHorarioTecnico> findByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Optional<ExcepcionHorarioTecnico> findByIdExcepcion(UUID idExcepcion);

    @Query("SELECT e FROM ExcepcionHorarioTecnico e WHERE e.idUsuarioTecnico = :idUsuarioTecnico " +
           "AND ((e.fechaInicio <= :fecha AND e.fechaFin >= :fecha) OR " +
           "(e.fechaInicio >= :fechaInicio AND e.fechaFin <= :fechaFin) OR " +
           "(e.fechaInicio <= :fechaFin AND e.fechaFin >= :fechaFin))")
    List<ExcepcionHorarioTecnico> findExcepcionesEnRango(
            @Param("idUsuarioTecnico") UUID idUsuarioTecnico,
            @Param("fecha") ZonedDateTime fecha,
            @Param("fechaInicio") ZonedDateTime fechaInicio,
            @Param("fechaFin") ZonedDateTime fechaFin);

    @Query("SELECT e FROM ExcepcionHorarioTecnico e WHERE e.idUsuarioTecnico = :idUsuarioTecnico " +
           "AND e.fechaInicio > :fechaActual ORDER BY e.fechaInicio")
    List<ExcepcionHorarioTecnico> findExcepcionesFuturas(@Param("idUsuarioTecnico") UUID idUsuarioTecnico, @Param("fechaActual") ZonedDateTime fechaActual);

    void deleteByIdUsuarioTecnico(UUID idUsuarioTecnico);
}
