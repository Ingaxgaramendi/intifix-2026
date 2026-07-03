package com.intifix.modules.apelaciones.repository;

import com.intifix.modules.apelaciones.entity.Apelacion;
import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApelacionRepository extends JpaRepository<Apelacion, UUID> {
    Page<Apelacion> findAllByOrderByFechaEnvioDesc(Pageable pageable);
    Page<Apelacion> findByEstadoOrderByFechaEnvioDesc(EstadoApelacion estado, Pageable pageable);
    long countByEstado(EstadoApelacion estado);
}
