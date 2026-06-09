package com.intifix.modules.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "correo", unique = true, nullable = false, length = 255)
    private String correo;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "telefono", unique = true, nullable = false, length = 20)
    private String telefono;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false, columnDefinition = "estado_usuario")
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Builder.Default
    @Column(name = "verificado", nullable = false)
    private boolean verificado = false;

    @Builder.Default
    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    @Column(name = "ultimo_login", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime ultimoLogin;

    @Column(name = "fecha_registro", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime fechaRegistro;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "id_usuario"))
    @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private Set<RolUsuario> roles = new HashSet<>();

    public String getClave() {
        return passwordHash;
    }

    public void setClave(String clave) {
        this.passwordHash = clave;
    }
}
