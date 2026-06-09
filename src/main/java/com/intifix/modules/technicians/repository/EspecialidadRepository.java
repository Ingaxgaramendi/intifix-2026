package com.intifix.modules.technicians.repository;

import com.intifix.modules.technicians.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EspecialidadRepository extends JpaRepository < Especialidad, UUID > {}
