package com.intifix.modules.ai.entity;

import jakarta.persistence.Column;
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
@Table(name = "diagnosticos_ia")
public class DiagnosticoIa {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "servicio_id", nullable = false)
    private UUID servicioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaDiagnostico categoria;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal confianza;

    @Column(nullable = false, length = 2000)
    private String resumen;

    @Column(nullable = false, length = 80)
    private String modelo = "intifix-mock-v1";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() {
        return id;
    }

    public UUID getServicioId() {
        return servicioId;
    }

    public void setServicioId(UUID servicioId) {
        this.servicioId = servicioId;
    }

    public CategoriaDiagnostico getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDiagnostico categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getConfianza() {
        return confianza;
    }

    public void setConfianza(BigDecimal confianza) {
        this.confianza = confianza;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getModelo() {
        return modelo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
