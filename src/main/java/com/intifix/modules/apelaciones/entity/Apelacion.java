package com.intifix.modules.apelaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "apelaciones")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Apelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_apelacion", updatable = false, nullable = false)
    private UUID idApelacion;

    @Column(name = "correo", nullable = false, length = 255)
    private String correo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false)
    private TipoApelacion tipo;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoApelacion estado = EstadoApelacion.PENDIENTE;

    @Column(name = "nota_admin", columnDefinition = "TEXT")
    private String notaAdmin;

    @CreationTimestamp
    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private ZonedDateTime fechaEnvio;
}
