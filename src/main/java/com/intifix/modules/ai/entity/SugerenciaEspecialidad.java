package com.intifix.modules.ai.entity;

import com.intifix.modules.technicians.entity.EspecialidadTecnico;
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
@Table(name = "sugerencias_especialidad")
public class SugerenciaEspecialidad {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "diagnostico_id", nullable = false)
    private UUID diagnosticoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EspecialidadTecnico especialidad;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() {
        return id;
    }

    public UUID getDiagnosticoId() {
        return diagnosticoId;
    }

    public void setDiagnosticoId(UUID diagnosticoId) {
        this.diagnosticoId = diagnosticoId;
    }

    public EspecialidadTecnico getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(EspecialidadTecnico especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
