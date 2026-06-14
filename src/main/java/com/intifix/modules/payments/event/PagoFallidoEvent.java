package com.intifix.modules.payments.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PagoFallidoEvent extends ApplicationEvent {

    private final UUID idPago;
    private final UUID idServicio;
    private final String motivoFallo;
    private final BigDecimal montoTotal;
    private final ZonedDateTime fechaFallo;

    public PagoFallidoEvent(Object source, UUID idPago, UUID idServicio,
                           String motivoFallo, BigDecimal montoTotal,
                           ZonedDateTime fechaFallo) {
        super(source);
        this.idPago = idPago;
        this.idServicio = idServicio;
        this.motivoFallo = motivoFallo;
        this.montoTotal = montoTotal;
        this.fechaFallo = fechaFallo;
    }

    public UUID getIdPago() {
        return idPago;
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public String getMotivoFallo() {
        return motivoFallo;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public ZonedDateTime getFechaFallo() {
        return fechaFallo;
    }
}
