package com.intifix.modules.payments.repository;

import com.intifix.modules.payments.entity.EstadoPago;
import com.intifix.modules.payments.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID>, JpaSpecificationExecutor<Pago> {

    boolean existsByIdServicio(UUID idServicio);

    Optional<Pago> findByIdServicio(UUID idServicio);

    Page<Pago> findByEstado(EstadoPago estado, Pageable pageable);

    List<Pago> findByEstado(EstadoPago estado);

    Optional<Pago> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Pago p WHERE p.estado = :estado")
    List<Pago> findPagosConfirmados(@Param("estado") EstadoPago estado);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = :estado")
    Long contarPagosPorEstado(@Param("estado") EstadoPago estado);

    @Query("SELECT SUM(p.montoTotal) FROM Pago p WHERE p.estado = :estado")
    Optional<BigDecimal> obtenerMontoTotalProcesado(@Param("estado") EstadoPago estado);

    @Query("SELECT p FROM Pago p WHERE p.estado = 'PENDIENTE'")
    List<Pago> findPagosPendientes();

    @Query("SELECT p FROM Pago p WHERE p.estado = 'PAGADO'")
    List<Pago> findPagosConfirmados();
}
