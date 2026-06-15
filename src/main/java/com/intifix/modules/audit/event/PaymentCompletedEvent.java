package com.intifix.modules.audit.event;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Se publica cuando un pago se completa con éxito.
 */
public record PaymentCompletedEvent(
        UUID paymentId,
        UUID serviceId,
        UUID userId,
        BigDecimal monto
) {}
