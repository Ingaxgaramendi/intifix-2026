package com.intifix.modules.payments.repository;

import com.intifix.modules.payments.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, UUID> {
    Optional<Factura> findByPagoId(UUID pagoId);
}
