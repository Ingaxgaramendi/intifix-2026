package com.intifix.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
// Las columnas estado y rol son enums NATIVOS de PostgreSQL (estado_usuario,
// rol_usuario): sin NAMED_ENUM, Hibernate envía varchar y el INSERT falla.

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
// No ZonedDateTime: suspensionHasta uses LocalDateTime to match the table timezone (server-local).

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAuth {

    @Id
    @Column(name = "id_usuario", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idUsuario;

    @Column(name = "correo", nullable = false, unique = true, length = 255)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "telefono", nullable = false, unique = true, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "verificado", nullable = false)
    @Builder.Default
    private Boolean verificado = false;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    /** Cuándo se levanta automáticamente la suspensión. NULL si es indefinida (ban) o cuenta activa. */
    @Column(name = "suspension_hasta")
    private LocalDateTime suspensionHasta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "id_usuario")
    )
    @Column(name = "rol", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private Set<RolUsuario> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (idUsuario == null) {
            idUsuario = UUID.randomUUID();
        }
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoUsuario.ACTIVO;
        }
        if (verificado == null) {
            verificado = false;
        }
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }
    }
}
