package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "perfiles_tecnico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilTecnicoOperativo {

    @Id
    @Column(name = "id_usuario")
    private UUID usuarioId;

    @Column(name = "nombres_completos", nullable = false, length = 255)
    private String nombresCompletos;

    @Column(name = "dni_ruc", nullable = false, unique = true, length = 20)
    private String dniRuc;

    @Column(name = "experiencia_anios", nullable = false)
    @Builder.Default
    private Integer experienciaAnios = 0;

    @Column(name = "estado_aprobacion", nullable = false, length = 50)
    private String estadoAprobacion; // Se mapea como String para interactuar con el ENUM PostgreSQL nativo

    @Column(name = "disponibilidad", nullable = false, length = 50)
    private String disponibilidad; // Se mapea como String para interactuar con el ENUM PostgreSQL nativo

    @Column(name = "tarifa_base", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tarifaBase = BigDecimal.ZERO;

    @Column(name = "dni_frontal_url", nullable = false, columnDefinition = "TEXT")
    private String dniFrontalUrl;

    @Column(name = "dni_trasero_url", nullable = false, columnDefinition = "TEXT")
    private String dniTraseroUrl;

    @Column(name = "antecedente_penal_url", nullable = false, columnDefinition = "TEXT")
    private String referentePenalUrl;

    @Column(name = "certificado_tecnico_url", columnDefinition = "TEXT")
    private String certificadoTecnicoUrl;

    @Column(name = "creado_en", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime creadoEn = OffsetDateTime.now();

    // Relación One-to-One con su tabla de analíticas separada
    @OneToOne(mappedBy = "tecnico", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private ReputacionTecnico reputacion;

    // Relación Many-to-Many exacta con tu tabla intermedia 'tecnico_specialidad'
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tecnico_especialidad",
        joinColumns = @JoinColumn(name = "id_usuario_tecnico"),
        inverseJoinColumns = @JoinColumn(name = "id_especialidad")
    )
    @Builder.Default
    private Set < Especialidad > especialidades = new HashSet <> ();

    // Relación One-to-Many limpia con cascada para tus bloques horarios
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set < HorarioTecnico > horarios = new HashSet <> ();
}
