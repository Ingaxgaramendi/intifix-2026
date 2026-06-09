package com.intifix.modules.services.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.intifix.modules.services.entity.enums.EstadoServicio;
import com.intifix.modules.services.entity.enums.ModalidadServicio;
import com.intifix.modules.services.entity.enums.PrioridadServicio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "servicios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @Column(name = "id_servicio")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_cliente", nullable = false)
    private UUID idCliente;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // MAPEO ENUM NATIVO: modalidad_servicio
    @Column(nullable = false, columnDefinition = "modalidad_servicio")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ModalidadServicio modalidad;

    // MAPEO ENUM NATIVO: prioridad_servicio
    @Column(nullable = false, columnDefinition = "prioridad_servicio")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private PrioridadServicio prioridad = PrioridadServicio.MEDIA;

    // MAPEO ENUM NATIVO: estado_servicio ('PENDIENTE', 'COTIZANDO', 'ASIGNADO', 'EN_PROCESO', 'FINALIZADO', 'CANCELADO')
    @Column(nullable = false, columnDefinition = "estado_servicio")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private EstadoServicio estado = EstadoServicio.PENDIENTE;

    @Column(name = "presupuesto_maximo", precision = 10, scale = 2)
    private BigDecimal presupuestoMaximo;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
}
