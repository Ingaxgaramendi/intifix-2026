package com.intifix.modules.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "perfiles_tecnico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilTecnico {

    @Id
    @Column(name = "id_usuario", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombres_completos", nullable = false, length = 255)
    private String nombresCompletos;

    @Column(name = "dni_ruc", nullable = false, unique = true, length = 20)
    private String dniRuc;

    @Column(name = "experiencia_anios", nullable = false)
    @Builder.Default
    private Integer experienciaAnios = 0;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_aprobacion", nullable = false, columnDefinition = "estado_aprobacion_tecnico")
    @Builder.Default
    private EstadoAprobacionTecnico estadoAprobacion = EstadoAprobacionTecnico.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "disponibilidad", nullable = false, columnDefinition = "disponibilidad_tecnico")
    @Builder.Default
    private DisponibilidadTecnico disponibilidad = DisponibilidadTecnico.DISPONIBLE;

    @Column(name = "tarifa_base", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tarifaBase = BigDecimal.ZERO;

    @Column(name = "dni_frontal_url", nullable = false, columnDefinition = "TEXT")
    private String dniFrontalUrl;

    @Column(name = "dni_trasero_url", nullable = false, columnDefinition = "TEXT")
    private String dniTraseroUrl;

    @Column(name = "antecedente_penal_url", nullable = false, columnDefinition = "TEXT")
    private String antecedentePenalUrl;

    @Column(name = "certificado_tecnico_url", columnDefinition = "TEXT")
    private String certificadoTecnicoUrl;

    @Column(name = "creado_en", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime creadoEn;
}
