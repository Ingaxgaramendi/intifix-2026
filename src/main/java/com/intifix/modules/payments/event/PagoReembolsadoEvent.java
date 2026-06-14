package com.intifix.modules.payments.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PagoReembolsadoEvent extends ApplicationEvent {

    private final UUID idPago;
    private final UUID idServicio;
    private final String transactionId;
    private final String razon;
    private final BigDecimal montoReembolsado;
    private final ZonedDateTime fechaReembolso;

    public PagoReembolsadoEvent(Object source, UUID idPago, UUID idServicio,
                                String transactionId, String razon,
                                BigDecimal montoReembolsado, ZonedDateTime fechaReembolso) {
        super(source);
        this.idPago = idPago;
        this.idServicio = idServicio;
        this.transactionId = transactionId;
        this.razon = razon;
        this.montoReembolsado = montoReembolsado;
        this.fechaReembolso = fechaReembolso;
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

    public String getRazon() {
        return razon;
    }

    public BigDecimal getMontoReembolsado() {
        return montoReembolsado;
    }

    public ZonedDateTime getFechaReembolso() {
        return fechaReembolso;
    }
}
