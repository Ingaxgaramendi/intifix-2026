package com.intifix.modules.payments.event;

import com.intifix.modules.payments.entity.TipoComprobante;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class FacturaEmitidaEvent extends ApplicationEvent {

    private final UUID idFactura;
    private final UUID idPago;
    private final UUID idServicio;
    private final String codigoComprobante;
    private final TipoComprobante tipo;
    private final BigDecimal total;
    private final ZonedDateTime fechaEmision;

    public FacturaEmitidaEvent(Object source, UUID idFactura, UUID idPago, UUID idServicio,
                              String codigoComprobante, TipoComprobante tipo,
                              BigDecimal total, ZonedDateTime fechaEmision) {
        super(source);
        this.idFactura = idFactura;
        this.idPago = idPago;
        this.idServicio = idServicio;
        this.codigoComprobante = codigoComprobante;
        this.tipo = tipo;
        this.total = total;
        this.fechaEmision = fechaEmision;
    }

    public UUID getIdFactura() {
        return idFactura;
    }

    public UUID getIdPago() {
        return idPago;
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public String getCodigoComprobante() {
        return codigoComprobante;
    }

    public TipoComprobante getTipo() {
        return tipo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public ZonedDateTime getFechaEmision() {
        return fechaEmision;
    }
}
