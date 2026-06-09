package com.intifix.modules.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID servicioId,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        String moneda,
        UUID metodoPagoId
) {
}
