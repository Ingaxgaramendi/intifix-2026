package com.intifix.modules.payments.entity;

import com.intifix.shared.converter.JsonNodeAttributeConverter;
import com.intifix.shared.entity.AuditedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class Pago extends AuditedEntity {

    @Id
    @UuidGenerator
    @Column(name = "id_pago")
    private UUID id;

    @Column(name = "id_servicio", nullable = false)
    private UUID servicioId;

    @Column(name = "id_metodo_pago")
    private UUID metodoPagoId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "payee_id")
    private UUID payeeId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "referencia", length = 255)
    private String referencia;

    @Column(name = "gateway_respuesta")
    @Convert(converter = JsonNodeAttributeConverter.class)
    private com.fasterxml.jackson.databind.JsonNode gatewayRespuesta;

    @Column(name = "metodo_pago_tipo", length = 50)
    private String metodoPagoTipo;

    @Column(name = "comision_plataforma", precision = 12, scale = 2)
    private BigDecimal comisionPlataforma = BigDecimal.ZERO;

    @Column(name = "neto_pagado", precision = 12, scale = 2)
    private BigDecimal netoPagado;

    @Column(name = "motivo_fallo")
    private String motivoFallo;

    @Column(name = "reintentos_fallidos")
    private Integer reintentosFallidos = 0;

    @Column(name = "fecha_pago")
    private Instant pagadoAt;

    @Column(name = "fecha_vencimiento")
    private Instant fechaVencimiento;

    @Column(name = "reembolso_fecha")
    private Instant reembolsoFecha;

    @Column(name = "reembolso_monto", precision = 12, scale = 2)
    private BigDecimal reembolsoMonto;

    @Column(name = "reembolso_razon")
    private String reembolsoRazon;

    @Column(name = "refund_reason_code", length = 100)
    private String refundReasonCode;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeAttributeConverter.class)
    private com.fasterxml.jackson.databind.JsonNode metadata;

    public UUID getId() {
        return id;
    }

    public UUID getServicioId() {
        return servicioId;
    }

    public void setServicioId(UUID servicioId) {
        this.servicioId = servicioId;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public UUID getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(UUID payeeId) {
        this.payeeId = payeeId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public UUID getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(UUID metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public com.fasterxml.jackson.databind.JsonNode getGatewayRespuesta() {
        return gatewayRespuesta;
    }

    public void setGatewayRespuesta(com.fasterxml.jackson.databind.JsonNode gatewayRespuesta) {
        this.gatewayRespuesta = gatewayRespuesta;
    }

    public String getMetodoPagoTipo() {
        return metodoPagoTipo;
    }

    public void setMetodoPagoTipo(String metodoPagoTipo) {
        this.metodoPagoTipo = metodoPagoTipo;
    }

    public BigDecimal getComisionPlataforma() {
        return comisionPlataforma;
    }

    public void setComisionPlataforma(BigDecimal comisionPlataforma) {
        this.comisionPlataforma = comisionPlataforma;
    }

    public BigDecimal getNetoPagado() {
        return netoPagado;
    }

    public void setNetoPagado(BigDecimal netoPagado) {
        this.netoPagado = netoPagado;
    }

    public String getMotivoFallo() {
        return motivoFallo;
    }

    public void setMotivoFallo(String motivoFallo) {
        this.motivoFallo = motivoFallo;
    }

    public Integer getReintentosFallidos() {
        return reintentosFallidos;
    }

    public void setReintentosFallidos(Integer reintentosFallidos) {
        this.reintentosFallidos = reintentosFallidos;
    }

    public Instant getPagadoAt() {
        return pagadoAt;
    }

    public void setPagadoAt(Instant pagadoAt) {
        this.pagadoAt = pagadoAt;
    }

    public Instant getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Instant fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Instant getReembolsoFecha() {
        return reembolsoFecha;
    }

    public void setReembolsoFecha(Instant reembolsoFecha) {
        this.reembolsoFecha = reembolsoFecha;
    }

    public BigDecimal getReembolsoMonto() {
        return reembolsoMonto;
    }

    public void setReembolsoMonto(BigDecimal reembolsoMonto) {
        this.reembolsoMonto = reembolsoMonto;
    }

    public String getReembolsoRazon() {
        return reembolsoRazon;
    }

    public void setReembolsoRazon(String reembolsoRazon) {
        this.reembolsoRazon = reembolsoRazon;
    }

    public String getRefundReasonCode() {
        return refundReasonCode;
    }

    public void setRefundReasonCode(String refundReasonCode) {
        this.refundReasonCode = refundReasonCode;
    }

    public com.fasterxml.jackson.databind.JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(com.fasterxml.jackson.databind.JsonNode metadata) {
        this.metadata = metadata;
    }
}
