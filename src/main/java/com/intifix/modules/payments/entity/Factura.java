package com.intifix.modules.payments.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "facturas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_factura", updatable = false, nullable = false)
    private UUID idFactura;

    @Column(name = "id_pago", nullable = false)
    private UUID idPago;

    @Column(name = "codigo_comprobante", nullable = false, unique = true, length = 100)
    private String codigoComprobante;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false)
    private TipoComprobante tipo = TipoComprobante.BOLETA;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_fiscal", nullable = false)
    private EstadoFiscalComprobante estadoFiscal = EstadoFiscalComprobante.PENDIENTE;

    @Column(name = "url_pdf", columnDefinition = "TEXT")
    private String urlPdf;

    @Column(name = "id_factura_referencia")
    private UUID idFacturaReferencia;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private ZonedDateTime fechaEmision;

    @PrePersist
    protected void onCreate() {
        fechaEmision = ZonedDateTime.now();
    }
}