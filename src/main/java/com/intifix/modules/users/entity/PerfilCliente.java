package com.intifix.modules.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perfiles_cliente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCliente implements Persistable<UUID> {

    @Id
    @Column(name = "id_usuario", nullable = false, updatable = false)
    private UUID idUsuario;

    @Column(name = "nombres_completos", nullable = false, length = 255)
    private String nombresCompletos;

    @Column(name = "dni_ruc", unique = true, length = 20)
    private String dniRuc;

    @Column(name = "foto_perfil_url", columnDefinition = "TEXT")
    private String fotoPerfilUrl;

    /** Ubicación base/guardada del cliente (FK a ubicaciones). Opcional. */
    @Column(name = "id_ubicacion")
    private UUID idUbicacion;

    /**
     * Generado por PostgreSQL ({@code DEFAULT CURRENT_TIMESTAMP}).
     * Hibernate excluye la columna del INSERT y la relee vía RETURNING.
     */
    @Generated(event = EventType.INSERT)
    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Override
    public UUID getId() {
        return idUsuario;
    }

    /**
     * El id se asigna manualmente (proviene del módulo de identidad), por lo que
     * Spring Data no puede inferir si la entidad es nueva. Sin esto, cada save()
     * de una entidad nueva ejecutaría un SELECT previo (merge) innecesario.
     */
    @Override
    @Transient
    public boolean isNew() {
        return creadoEn == null;
    }
}
