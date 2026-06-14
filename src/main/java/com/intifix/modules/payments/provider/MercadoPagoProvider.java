package com.intifix.modules.payments.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MercadoPagoProvider implements PaymentProvider {

    @Override
    public PaymentResult procesarPago(PaymentRequest request) {
        log.info("Procesando pago con Mercado Pago para servicio: {}", request.idServicio());
        
        try {
            String transactionId = "MP-" + UUID.randomUUID().toString();
            
            Map<String, Object> datosRespuesta = new HashMap<>();
            datosRespuesta.put("provider", "MercadoPago");
            datosRespuesta.put("transaction_id", transactionId);
            datosRespuesta.put("monto", request.monto());
            datosRespuesta.put("moneda", "PEN");
            
            return new PaymentResult(
                    true,
                    transactionId,
                    "Pago procesado exitosamente con Mercado Pago",
                    PaymentStatus.APROBADO,
                    datosRespuesta
            );
        } catch (Exception e) {
            log.error("Error procesando pago con Mercado Pago", e);
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
        log.info("Confirmando pago con Mercado Pago: {}", transactionId);
        
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put("provider", "MercadoPago");
        datosRespuesta.put("transaction_id", transactionId);
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
        log.info("Reembolsando pago con Mercado Pago: {}, razon: {}", transactionId, razon);
        
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put("provider", "MercadoPago");
        datosRespuesta.put("transaction_id", transactionId);
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
        log.info("Consultando estado de pago con Mercado Pago: {}", transactionId);
        return PaymentStatus.APROBADO;
    }
}
