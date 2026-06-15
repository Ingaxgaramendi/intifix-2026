package com.intifix.modules.audit.event;

import java.util.UUID;

/**
 * Se publica al emitirse una factura.
 */
public record InvoiceGeneratedEvent(
        UUID invoiceId,
        UUID paymentId,
        UUID userId
) {}
