package com.intifix.modules.ai.repository;

import com.intifix.modules.ai.entity.SugerenciaEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SugerenciaEspecialidadRepository extends JpaRepository<SugerenciaEspecialidad, UUID> {
    List<SugerenciaEspecialidad> findByDiagnosticoId(UUID diagnosticoId);
}
