package com.intifix.modules.quotes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateQuoteRequest(
        @NotNull UUID servicioId,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @Size(max = 3) String moneda,
        @Size(max = 1000) String mensaje,
        Integer validezHoras
) {
}
