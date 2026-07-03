package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.TecnicoEspecialidad;
import com.intifix.modules.technicians.entity.TecnicoEspecialidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TecnicoEspecialidadRepository extends JpaRepository<TecnicoEspecialidad, TecnicoEspecialidadId> {

    List<TecnicoEspecialidad> findByIdUsuarioTecnico(UUID idUsuarioTecnico);

    Optional<TecnicoEspecialidad> findByIdUsuarioTecnicoAndIdEspecialidad(UUID idUsuarioTecnico, UUID idEspecialidad);

    List<TecnicoEspecialidad> findByIdEspecialidad(UUID idEspecialidad);

    boolean existsByIdUsuarioTecnicoAndIdEspecialidad(UUID idUsuarioTecnico, UUID idEspecialidad);

    @Query("SELECT te.idEspecialidad FROM TecnicoEspecialidad te WHERE te.idUsuarioTecnico = :idUsuarioTecnico")
    List<UUID> findEspecialidadIdsByTecnico(@Param("idUsuarioTecnico") UUID idUsuarioTecnico);

    @Query("SELECT te.idUsuarioTecnico FROM TecnicoEspecialidad te WHERE te.idEspecialidad = :idEspecialidad")
    List<UUID> findTecnicoIdsByEspecialidad(@Param("idEspecialidad") UUID idEspecialidad);

    void deleteByIdUsuarioTecnico(UUID idUsuarioTecnico);

    void deleteByIdEspecialidad(UUID idEspecialidad);

    void deleteByIdUsuarioTecnicoAndIdEspecialidad(UUID idUsuarioTecnico, UUID idEspecialidad);
}
