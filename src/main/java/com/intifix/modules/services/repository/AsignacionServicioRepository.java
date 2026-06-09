package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.AsignacionServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AsignacionServicioRepository extends JpaRepository < AsignacionServicio, UUID > {}
