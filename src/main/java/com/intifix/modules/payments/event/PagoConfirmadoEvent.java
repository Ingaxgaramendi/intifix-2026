package com.intifix.modules.payments.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PagoConfirmadoEvent extends ApplicationEvent {

    private final UUID idPago;
    private final UUID idServicio;
    private final String transactionId;
    private final BigDecimal montoTotal;
    private final ZonedDateTime fechaPago;

    public PagoConfirmadoEvent(Object source, UUID idPago, UUID idServicio,
                               String transactionId, BigDecimal montoTotal,
                               ZonedDateTime fechaPago) {
        super(source);
        this.idPago = idPago;
        this.idServicio = idServicio;
        this.transactionId = transactionId;
        this.montoTotal = montoTotal;
        this.fechaPago = fechaPago;
    }

    public UUID getIdPago() {
        return idPago;
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public ZonedDateTime getFechaPago() {
        return fechaPago;
    }
}
