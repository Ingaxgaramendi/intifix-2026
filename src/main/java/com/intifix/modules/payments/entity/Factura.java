package com.intifix.modules.payments.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.intifix.shared.converter.JsonNodeAttributeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @UuidGenerator
    @Column(name = "id_factura", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "id_pago", nullable = false, unique = true, updatable = false)
    private UUID pagoId;

    @Column(name = "cliente_id", nullable = false, updatable = false)
    private UUID clienteId;

    @Column(name = "proveedor_id")
    private UUID proveedorId;

    @Column(name = "codigo_factura", nullable = false, unique = true, updatable = false, length = 100)
    private String codigoFactura;

    @Column(name = "numero", nullable = false, unique = true, updatable = false, length = 100)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_factura", nullable = false, length = 50)
    private EstadoFactura estadoFactura = EstadoFactura.BORRADOR;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "descripcion_items")
    private String descripcionItems;

    @Column(name = "condiciones_pago", length = 500)
    private String condicionesPago;

    @Column(name = "fecha_pago_esperada")
    private Instant fechaPagoEsperada;

    @Column(name = "url_pdf", length = 500)
    private String urlPdf;

    @Column(name = "html_factura", columnDefinition = "TEXT")
    private String htmlFactura;

    @Column(name = "xml_factura", columnDefinition = "TEXT")
    private String xmlFactura;

    @Column(name = "url_electronica", length = 500)
    private String urlElectronica;

    @Column(name = "numero_electronico", length = 100)
    private String numeroElectronica;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private Instant emitidaAt;

    @Column(name = "fecha_vencimiento")
    private Instant fechaVencimiento;

    @Column(name = "fecha_anulacion")
    private Instant fechaAnulacion;

    @Column(name = "razon_anulacion")
    private String razonAnulacion;

    @Convert(converter = JsonNodeAttributeConverter.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    // =========================
    // FACTORY METHOD (CREACIÓN CONTROLADA)
    // =========================
    public static Factura crear(
            UUID pagoId,
            UUID clienteId,
            UUID proveedorId,
            String codigoFactura,
            String numero,
            BigDecimal subtotal,
            BigDecimal impuesto,
            BigDecimal total
    ) {
        Factura factura = new Factura();
        factura.pagoId = pagoId;
        factura.clienteId = clienteId;
        factura.proveedorId = proveedorId;
        factura.codigoFactura = codigoFactura;
        factura.numero = numero;
        factura.subtotal = subtotal;
        factura.impuesto = impuesto != null ? impuesto : BigDecimal.ZERO;
        factura.total = total;
        factura.estadoFactura = EstadoFactura.BORRADOR;
        factura.emitidaAt = Instant.now();
        return factura;
    }

    // =========================
    // DOMAIN LOGIC (NO SETTERS LIBRES)
    // =========================

    public void actualizarProveedor(UUID proveedorId) {
        this.proveedorId = proveedorId;
    }

    public void actualizarCondicionesPago(String condiciones) {
        this.condicionesPago = condiciones;
    }

    public void programarVencimiento(Instant fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void emitir() {
        if (this.estadoFactura != EstadoFactura.BORRADOR) {
            throw new IllegalStateException("Solo se puede emitir una factura en borrador");
        }
        this.estadoFactura = EstadoFactura.EMITIDA;
        this.emitidaAt = Instant.now();
    }

    public void anular(String razon) {
        if (this.estadoFactura == EstadoFactura.ANULADA) {
            throw new IllegalStateException("La factura ya está anulada");
        }
        this.estadoFactura = EstadoFactura.ANULADA;
        this.fechaAnulacion = Instant.now();
        this.razonAnulacion = razon;
    }

    public void adjuntarPdf(String urlPdf) {
        this.urlPdf = urlPdf;
    }

    public void adjuntarElectronico(String url, String numero) {
        this.urlElectronica = url;
        this.numeroElectronica = numero;
    }
}