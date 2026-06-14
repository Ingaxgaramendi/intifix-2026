package com.intifix.modules.payments.config;

import com.intifix.modules.payments.event.FacturaEmitidaEvent;
import com.intifix.modules.payments.event.PagoCreadoEvent;
import com.intifix.modules.payments.event.PagoConfirmadoEvent;
import com.intifix.modules.payments.event.PagoFallidoEvent;
import com.intifix.modules.payments.event.PagoReembolsadoEvent;
import com.intifix.modules.payments.service.interfaces.FacturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final FacturaService facturaService;

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
        
        try {
            generarFacturaAutomatica(event);
        } catch (Exception e) {
            log.error("Error al generar factura automática para pago: {}", event.getIdPago(), e);
        }
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

    @EventListener
    public void handleFacturaEmitida(FacturaEmitidaEvent event) {
        log.info("Factura emitida - ID: {}, Pago: {}, Tipo: {}, Código: {}", 
                event.getIdFactura(), event.getIdPago(), event.getTipo(), event.getCodigoComprobante());
    }

    private void generarFacturaAutomatica(PagoConfirmadoEvent event) {
        if (event.getIdPago() != null) {
            com.intifix.modules.payments.dto.request.CrearFacturaRequest request = 
                    com.intifix.modules.payments.dto.request.CrearFacturaRequest.builder()
                            .idPago(event.getIdPago())
                            .tipo(com.intifix.modules.payments.entity.TipoComprobante.BOLETA)
                            .build();
            
            facturaService.crearFactura(request);
            log.info("Factura automática generada para pago: {}", event.getIdPago());
        }
    }
}
