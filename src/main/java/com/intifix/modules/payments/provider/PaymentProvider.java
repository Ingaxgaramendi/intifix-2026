package com.intifix.modules.payments.provider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface PaymentProvider {

    PaymentResult procesarPago(PaymentRequest request);

    PaymentResult confirmarPago(String transactionId);

    PaymentResult reembolsarPago(String transactionId, String razon);

    PaymentStatus consultarEstado(String transactionId);

    record PaymentRequest(
            UUID idServicio,
            UUID idMetodoPago,
            BigDecimal monto,
            String descripcion,
            Map<String, String> metadata
    ) {}

    record PaymentResult(
            boolean exitoso,
            String transactionId,
            String mensaje,
            PaymentStatus estado,
            Map<String, Object> datosRespuesta
    ) {}

    enum PaymentStatus {
        PENDIENTE,
        APROBADO,
        RECHAZADO,
        FALLIDO,
        REEMBOLSADO
    }
}
