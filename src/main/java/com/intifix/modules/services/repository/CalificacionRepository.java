package com.intifix.modules.services.repository;

import com.intifix.modules.services.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CalificacionRepository extends JpaRepository < Calificacion, UUID > {}
