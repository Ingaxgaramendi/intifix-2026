package com.intifix.modules.payments.repository;

import com.intifix.modules.payments.entity.EstadoFiscalComprobante;
import com.intifix.modules.payments.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    Optional<Factura> findByIdPago(UUID idPago);

    Optional<Factura> findByCodigoComprobante(String codigoComprobante);

    List<Factura> findByTipo(com.intifix.modules.payments.entity.TipoComprobante tipo);

    List<Factura> findByEstadoFiscal(EstadoFiscalComprobante estadoFiscal);

    @Query("SELECT f FROM Factura f WHERE f.estadoFiscal = 'PENDIENTE'")
    List<Factura> findFacturasPendientesSunat();

    List<Factura> findByIdFacturaReferencia(UUID idFacturaReferencia);
}
