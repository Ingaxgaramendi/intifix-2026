package com.intifix.modules.payments.dto;

import com.intifix.modules.payments.entity.EstadoPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        UUID servicioId,
        BigDecimal monto,
        String moneda,
        EstadoPago estado,
        String referencia,
        Instant pagadoAt,
        InvoiceDto factura
) {
    public record InvoiceDto(UUID id, String numero, BigDecimal total, String pdfUrl) {
    }
}
