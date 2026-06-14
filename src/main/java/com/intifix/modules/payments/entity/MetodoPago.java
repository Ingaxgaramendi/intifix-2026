package com.intifix.modules.payments.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "metodo_pago")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_metodo_pago", updatable = false, nullable = false)
    private UUID idMetodoPago;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
}
