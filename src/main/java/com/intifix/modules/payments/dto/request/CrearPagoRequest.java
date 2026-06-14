package com.intifix.modules.payments.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPagoRequest {

    @NotNull(message = "El ID del servicio es obligatorio")
    private UUID idServicio;

    @NotNull(message = "El ID del método de pago es obligatorio")
    private UUID idMetodoPago;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El monto total debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal montoTotal;

    @NotNull(message = "La comisión de plataforma es obligatoria")
    @DecimalMin(value = "0.00", message = "La comisión de plataforma debe ser mayor o igual a 0")
    @Digits(integer = 8, fraction = 2, message = "La comisión de plataforma debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal comisionPlataforma;

    @NotNull(message = "El monto neto del técnico es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto neto del técnico debe ser mayor o igual a 0")
    @Digits(integer = 8, fraction = 2, message = "El monto neto del técnico debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal montoNetoTecnico;

    @NotNull(message = "El impuesto total es obligatorio")
    @DecimalMin(value = "0.00", message = "El impuesto total debe ser mayor o igual a 0")
    @Digits(integer = 8, fraction = 2, message = "El impuesto total debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal impuestoTotal;
}
