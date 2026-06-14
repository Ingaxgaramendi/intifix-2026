package com.intifix.modules.payments.repository;

import com.intifix.modules.payments.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, UUID> {

    Optional<MetodoPago> findByNombre(String nombre);
}
