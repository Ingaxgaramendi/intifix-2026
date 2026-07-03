package com.intifix.modules.payments.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class StripeProvider implements PaymentProvider {

    private static final String PROVIDER_NAME = PROVIDER_NAME;
    private static final String KEY_PROVIDER = KEY_PROVIDER;
    private static final String KEY_TRANSACTION_ID = KEY_TRANSACTION_ID;

    @Override
    public PaymentResult procesarPago(PaymentRequest request) {
        log.info("Procesando pago con Stripe para servicio: {}", request.idServicio());
        
        try {
            String transactionId = "STRIPE-" + UUID.randomUUID().toString();
            
            Map<String, Object> datosRespuesta = new HashMap<>();
            datosRespuesta.put(KEY_PROVIDER, PROVIDER_NAME);
            datosRespuesta.put(KEY_TRANSACTION_ID, transactionId);
            datosRespuesta.put("monto", request.monto());
            datosRespuesta.put("moneda", "PEN");
            
            return new PaymentResult(
                    true,
                    transactionId,
                    "Pago procesado exitosamente con Stripe",
                    PaymentStatus.APROBADO,
                    datosRespuesta
            );
        } catch (Exception e) {
            log.error("Error procesando pago con Stripe", e);
            return new PaymentResult(
                    false,
                    null,
                    "Error al procesar pago: " + e.getMessage(),
                    PaymentStatus.FALLIDO,
                    Map.of()
            );
        }
    }

    @Override
    public PaymentResult confirmarPago(String transactionId) {
        log.info("Confirmando pago con Stripe: {}", transactionId);
        
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put(KEY_PROVIDER, PROVIDER_NAME);
        datosRespuesta.put(KEY_TRANSACTION_ID, transactionId);
        datosRespuesta.put("estado", "confirmado");
        
        return new PaymentResult(
                true,
                transactionId,
                "Pago confirmado exitosamente",
                PaymentStatus.APROBADO,
                datosRespuesta
        );
    }

    @Override
    public PaymentResult reembolsarPago(String transactionId, String razon) {
        log.info("Reembolsando pago con Stripe: {}, razon: {}", transactionId, razon);
        
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put(KEY_PROVIDER, PROVIDER_NAME);
        datosRespuesta.put(KEY_TRANSACTION_ID, transactionId);
        datosRespuesta.put("razon", razon);
        datosRespuesta.put("estado", "reembolsado");
        
        return new PaymentResult(
                true,
                transactionId,
                "Reembolso procesado exitosamente",
                PaymentStatus.REEMBOLSADO,
                datosRespuesta
        );
    }

    @Override
    public PaymentStatus consultarEstado(String transactionId) {
        log.info("Consultando estado de pago con Stripe: {}", transactionId);
        return PaymentStatus.APROBADO;
    }
}
