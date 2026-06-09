package com.intifix.modules.payments.repository;

import com.intifix.modules.payments.entity.EstadoPago;
import com.intifix.modules.payments.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    Page<Pago> findByEstado(EstadoPago estado, Pageable pageable);
}
