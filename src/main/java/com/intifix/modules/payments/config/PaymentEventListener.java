package com.intifix.modules.payments.config;

import com.intifix.modules.payments.email.ComprobanteEmailService;
import com.intifix.modules.payments.event.FacturaEmitidaEvent;
import com.intifix.modules.payments.event.PagoCreadoEvent;
import com.intifix.modules.payments.event.PagoConfirmadoEvent;
import com.intifix.modules.payments.event.PagoFallidoEvent;
import com.intifix.modules.payments.event.PagoReembolsadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final ComprobanteEmailService comprobanteEmailService;

    @EventListener
    public void handlePagoCreado(PagoCreadoEvent event) {
        log.info("Pago creado - ID: {}, Servicio: {}, Monto: {}",
                event.getIdPago(), event.getIdServicio(), event.getMontoTotal());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePagoConfirmado(PagoConfirmadoEvent event) {
        log.info("Pago confirmado - ID: {}, Servicio: {}, TransactionID: {}",
                event.getIdPago(), event.getIdServicio(), event.getTransactionId());
        // La factura la genera PagoServiceImpl.procesarPago directamente en el hilo
        // HTTP (con SecurityContext disponible). No se duplica aquí para evitar
        // llamar a crearFactura en un hilo async sin contexto de seguridad.
    }

    @EventListener
    public void handlePagoFallido(PagoFallidoEvent event) {
        log.error("Pago fallido - ID: {}, Servicio: {}, Motivo: {}",
                event.getIdPago(), event.getIdServicio(), event.getMotivoFallo());
    }

    @EventListener
    public void handlePagoReembolsado(PagoReembolsadoEvent event) {
        log.info("Pago reembolsado - ID: {}, Servicio: {}, Razón: {}",
                event.getIdPago(), event.getIdServicio(), event.getRazon());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleFacturaEmitida(FacturaEmitidaEvent event) {
        log.info("Factura emitida - ID: {}, Pago: {}, Tipo: {}, Código: {}",
                event.getIdFactura(), event.getIdPago(), event.getTipo(), event.getCodigoComprobante());
        comprobanteEmailService.enviarComprobante(event);
    }
}
