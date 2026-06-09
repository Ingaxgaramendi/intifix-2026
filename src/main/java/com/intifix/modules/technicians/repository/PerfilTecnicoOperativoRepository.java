package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.PerfilTecnicoOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PerfilTecnicoOperativoRepository extends JpaRepository < PerfilTecnicoOperativo, UUID > {

    // Query de negocio avanzado para listar técnicos aprobados y listos en base a filtros globales
    @Query("SELECT p FROM PerfilTecnicoOperativo p JOIN FETCH p.reputacion WHERE p.estadoAprobacion = 'APROBADO' AND p.disponibilidad = :disp")
    List < PerfilTecnicoOperativo > listarPorDisponibilidad(@Param("disp") String disponibilidad);
}
