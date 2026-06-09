package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.HistorialServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface HistorialServicioRepository extends JpaRepository < HistorialServicio, UUID > {}
