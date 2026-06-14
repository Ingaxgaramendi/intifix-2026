package com.intifix.modules.payments.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pago", updatable = false, nullable = false)
    private UUID idPago;

    @Column(name = "id_servicio", nullable = false, unique = true)
    private UUID idServicio;

    @Column(name = "id_metodo_pago", nullable = false)
    private UUID idMetodoPago;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "comision_plataforma", nullable = false, precision = 10, scale = 2)
    private BigDecimal comisionPlataforma;

    @Column(name = "monto_neto_tecnico", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoNetoTecnico;

    @Column(name = "impuesto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuestoTotal;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "fecha_pago")
    private ZonedDateTime fechaPago;

    // La BD genera creado_en con DEFAULT CURRENT_TIMESTAMP; Hibernate lo omite
    // en el INSERT (insertable=false) y lo lee de vuelta (@Generated).
    @Generated(event = EventType.INSERT)
    @Column(name = "creado_en", nullable = false, updatable = false, insertable = false)
    private ZonedDateTime creadoEn;
}
