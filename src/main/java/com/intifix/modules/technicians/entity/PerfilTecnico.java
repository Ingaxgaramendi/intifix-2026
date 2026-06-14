package com.intifix.modules.technicians.entity;

import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
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
    @Column(name = "id_usuario", nullable = false, updatable = false)
    private UUID idUsuario;

    @Column(name = "nombres_completos", nullable = false, length = 255)
    private String nombresCompletos;

    @Column(name = "dni_ruc", unique = true, length = 20)
    private String dniRuc;

    @Column(name = "experiencia_anios", nullable = false)
    private Integer experienciaAnios;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_aprobacion", nullable = false)
    @Builder.Default
    private EstadoAprobacionTecnico estadoAprobacion = EstadoAprobacionTecnico.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "disponibilidad", nullable = false)
    @Builder.Default
    private DisponibilidadTecnico disponibilidad = DisponibilidadTecnico.DISPONIBLE;

    @Column(name = "tarifa_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Column(name = "dni_frontal_url", columnDefinition = "TEXT")
    private String dniFrontalUrl;

    @Column(name = "dni_trasero_url", columnDefinition = "TEXT")
    private String dniTraseroUrl;

    @Column(name = "antecedente_penal_url", columnDefinition = "TEXT")
    private String antecedentePenalUrl;

    @Column(name = "certificado_tecnico_url", columnDefinition = "TEXT")
    private String certificadoTecnicoUrl;

    @Column(name = "id_ubicacion")
    private UUID idUbicacion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    @Builder.Default
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = ZonedDateTime.now();
        }
    }
}
