package com.intifix.modules.quotes.dto;

import com.intifix.modules.quotes.entity.EstadoCotizacion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QuoteDto(
        UUID id,
        UUID servicioId,
        UUID tecnicoId,
        BigDecimal monto,
        String moneda,
        String mensaje,
        EstadoCotizacion estado,
        Instant validezHasta,
        Instant createdAt
) {
}
