package com.intifix.modules.payments.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PagoCreadoEvent extends ApplicationEvent {

    private final UUID idPago;
    private final UUID idServicio;
    private final UUID idMetodoPago;
    private final BigDecimal montoTotal;
    private final BigDecimal comisionPlataforma;
    private final BigDecimal montoNetoTecnico;
    private final BigDecimal impuestoTotal;
    private final ZonedDateTime creadoEn;

    public PagoCreadoEvent(Object source, UUID idPago, UUID idServicio, UUID idMetodoPago,
                           BigDecimal montoTotal, BigDecimal comisionPlataforma,
                           BigDecimal montoNetoTecnico, BigDecimal impuestoTotal,
                           ZonedDateTime creadoEn) {
        super(source);
        this.idPago = idPago;
        this.idServicio = idServicio;
        this.idMetodoPago = idMetodoPago;
        this.montoTotal = montoTotal;
        this.comisionPlataforma = comisionPlataforma;
        this.montoNetoTecnico = montoNetoTecnico;
        this.impuestoTotal = impuestoTotal;
        this.creadoEn = creadoEn;
    }

    public UUID getIdPago() {
        return idPago;
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public UUID getIdMetodoPago() {
        return idMetodoPago;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public BigDecimal getComisionPlataforma() {
        return comisionPlataforma;
    }

    public BigDecimal getMontoNetoTecnico() {
        return montoNetoTecnico;
    }

    public BigDecimal getImpuestoTotal() {
        return impuestoTotal;
    }

    public ZonedDateTime getCreadoEn() {
        return creadoEn;
    }
}
