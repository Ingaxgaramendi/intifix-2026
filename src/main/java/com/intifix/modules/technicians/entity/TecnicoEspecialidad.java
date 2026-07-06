package com.intifix.modules.technicians.entity;

import com.intifix.modules.technicians.enums.EstadoCertificado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla de unión técnico-especialidad. PK compuesta: un técnico puede tener
 * varias especialidades y una especialidad varios técnicos. Cada asignación
 * lleva su propio certificado (URL en Cloudinary) que acredita ese oficio.
 */
@Entity
@Table(name = "tecnico_especialidad")
@IdClass(TecnicoEspecialidadId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoEspecialidad {

    @Id
    @Column(name = "id_usuario_tecnico", nullable = false, updatable = false)
    private UUID idUsuarioTecnico;

    @Id
    @Column(name = "id_especialidad", nullable = false, updatable = false)
    private UUID idEspecialidad;

    @Column(name = "certificado_url", columnDefinition = "TEXT")
    private String certificadoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_certificado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCertificado estadoCertificado = EstadoCertificado.PENDIENTE;
}
