package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.HorarioTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HorarioTecnicoRepository extends JpaRepository<HorarioTecnico, UUID>, JpaSpecificationExecutor<HorarioTecnico> {

    List<HorarioTecnico> findByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Optional<HorarioTecnico> findByIdHorario(UUID idHorario);

    boolean existsByIdUsuarioTecnicoAndDiaSemanaAndActivoTrue(UUID idUsuarioTecnico, Integer diaSemana);

    @Query("SELECT h FROM HorarioTecnico h WHERE h.idUsuarioTecnico = :idUsuarioTecnico AND h.diaSemana = :diaSemana AND h.activo = true")
    List<HorarioTecnico> findByTecnicoAndDiaSemana(@Param("idUsuarioTecnico") UUID idUsuarioTecnico, @Param("diaSemana") Integer diaSemana);

    @Query("SELECT h FROM HorarioTecnico h WHERE h.idUsuarioTecnico = :idUsuarioTecnico AND h.diaSemana = :diaSemana " +
           "AND ((h.horaInicio <= :horaInicio AND h.horaFin > :horaInicio) OR " +
           "(h.horaInicio < :horaFin AND h.horaFin >= :horaFin) OR " +
           "(h.horaInicio >= :horaInicio AND h.horaFin <= :horaFin)) AND h.activo = true")
    List<HorarioTecnico> findHorariosSolapados(
            @Param("idUsuarioTecnico") UUID idUsuarioTecnico,
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    @Query("SELECT h FROM HorarioTecnico h WHERE h.idUsuarioTecnico = :idUsuarioTecnico AND h.activo = true ORDER BY h.diaSemana, h.horaInicio")
    List<HorarioTecnico> findHorariosActivosByTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    void deleteByIdUsuarioTecnico(UUID idUsuarioTecnico);
}
