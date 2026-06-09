package com.intifix.modules.quotes.repository;

import com.intifix.modules.quotes.entity.Cotizacion;
import com.intifix.modules.quotes.entity.EstadoCotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CotizacionRepository extends JpaRepository<Cotizacion, UUID> {

    Page<Cotizacion> findByServicioId(UUID servicioId, Pageable pageable);

    List<Cotizacion> findByServicioIdAndEstado(UUID servicioId, EstadoCotizacion estado);

    Optional<Cotizacion> findByIdAndTecnicoId(UUID id, UUID tecnicoId);

    boolean existsByServicioIdAndTecnicoId(UUID servicioId, UUID tecnicoId);
}
