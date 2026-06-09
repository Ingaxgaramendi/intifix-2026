package com.intifix.modules.quotes.entity;

import com.intifix.shared.entity.AuditedEntity;
import com.intifix.shared.converter.JsonNodeAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cotizaciones")
public class Cotizacion extends AuditedEntity {

    @Id
    @UuidGenerator
    @Column(name = "id_cotizacion")
    private UUID id;

    @Column(name = "id_servicio", nullable = false)
    private UUID servicioId;

    @Column(name = "id_usuario_tecnico", nullable = false)
    private UUID tecnicoId;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(length = 1000)
    private String mensaje;

    @Column(name = "tiempo_respuesta_min")
    private Integer tiempoRespuestaMin;

    @Column(name = "validez_horas")
    private Integer validezHoras = 24;

    @Column(name = "aceptada_en")
    private Instant aceptadaEn;

    @Column(name = "rechazada_en")
    private Instant rechazadaEn;

    @Column(name = "razon_rechazo")
    private String razonRechazo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

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

    public UUID getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(UUID tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getTiempoRespuestaMin() {
        return tiempoRespuestaMin;
    }

    public void setTiempoRespuestaMin(Integer tiempoRespuestaMin) {
        this.tiempoRespuestaMin = tiempoRespuestaMin;
    }

    public Integer getValidezHoras() {
        return validezHoras;
    }

    public void setValidezHoras(Integer validezHoras) {
        this.validezHoras = validezHoras;
    }

    public Instant getAceptadaEn() {
        return aceptadaEn;
    }

    public void setAceptadaEn(Instant aceptadaEn) {
        this.aceptadaEn = aceptadaEn;
    }

    public Instant getRechazadaEn() {
        return rechazadaEn;
    }

    public void setRechazadaEn(Instant rechazadaEn) {
        this.rechazadaEn = rechazadaEn;
    }

    public String getRazonRechazo() {
        return razonRechazo;
    }

    public void setRazonRechazo(String razonRechazo) {
        this.razonRechazo = razonRechazo;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }

    public com.fasterxml.jackson.databind.JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(com.fasterxml.jackson.databind.JsonNode metadata) {
        this.metadata = metadata;
    }
}
