package com.intifix.modules.payments.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractPaymentProvider implements PaymentProvider {

    protected static final String KEY_PROVIDER = "provider";
    protected static final String KEY_TRANSACTION_ID = "transaction_id";

    // Logger bound to the concrete subclass, preserving class-level log context
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract String getProviderName();

    protected abstract String getTransactionPrefix();

    @Override
    public PaymentResult procesarPago(PaymentRequest request) {
        log.info("Procesando pago con {} para servicio: {}", getProviderName(), request.idServicio());
        try {
            String transactionId = getTransactionPrefix() + UUID.randomUUID().toString();
            Map<String, Object> datosRespuesta = buildBaseDatos(transactionId);
            datosRespuesta.put("monto", request.monto());
            datosRespuesta.put("moneda", "PEN");
            return new PaymentResult(
                    true,
                    transactionId,
                    "Pago procesado exitosamente con " + getProviderName(),
                    PaymentStatus.APROBADO,
                    datosRespuesta
            );
        } catch (Exception e) {
            log.error("Error procesando pago con {}", getProviderName(), e);
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
        log.info("Confirmando pago con {}: {}", getProviderName(), transactionId);
        Map<String, Object> datosRespuesta = buildBaseDatos(transactionId);
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
        log.info("Reembolsando pago con {}: {}, razon: {}", getProviderName(), transactionId, razon);
        Map<String, Object> datosRespuesta = buildBaseDatos(transactionId);
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
        log.info("Consultando estado de pago con {}: {}", getProviderName(), transactionId);
        return PaymentStatus.APROBADO;
    }

    private Map<String, Object> buildBaseDatos(String transactionId) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_PROVIDER, getProviderName());
        map.put(KEY_TRANSACTION_ID, transactionId);
        return map;
    }
}
