package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.ReputacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReputacionTecnicoRepository extends JpaRepository<ReputacionTecnico, UUID>, JpaSpecificationExecutor<ReputacionTecnico> {

    Optional<ReputacionTecnico> findByIdUsuarioTecnico(UUID idUsuarioTecnico);

    boolean existsByIdUsuarioTecnico(UUID idUsuarioTecnico);

    @Query("SELECT AVG(r.promedioCalificacion) FROM ReputacionTecnico r")
    java.math.BigDecimal calcularPromedioGeneral();

    @Query("SELECT r FROM ReputacionTecnico r ORDER BY r.promedioCalificacion DESC")
    java.util.List<ReputacionTecnico> findTopRated();
}
