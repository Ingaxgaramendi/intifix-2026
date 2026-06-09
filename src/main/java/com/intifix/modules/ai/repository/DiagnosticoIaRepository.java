package com.intifix.modules.ai.repository;

import com.intifix.modules.ai.entity.DiagnosticoIa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosticoIaRepository extends JpaRepository<DiagnosticoIa, UUID> {
    List<DiagnosticoIa> findByServicioIdOrderByCreatedAtDesc(UUID servicioId);
}
