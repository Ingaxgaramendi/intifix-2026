package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, UUID>, JpaSpecificationExecutor<Especialidad> {

    Optional<Especialidad> findByIdEspecialidad(UUID idEspecialidad);

    Optional<Especialidad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT e FROM Especialidad e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    java.util.List<Especialidad> buscarPorNombre(@Param("nombre") String nombre);
}
