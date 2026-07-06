package com.intifix.modules.services.event;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Domain event published when a quotation is accepted by a client.
 * 
 * This event is used to notify other modules about the acceptance of a quotation,
 * enabling cross-module coordination without direct dependencies.
 * 
 * In a future microservices architecture, this event would be published to Kafka
 * for asynchronous processing by interested consumers (e.g., payment module).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CotizacionAceptadaEvent {

    private final UUID idCotizacion;
    private final UUID idServicio;
    private final UUID idUsuarioTecnico;
    private final UUID idCliente;
    private final BigDecimal precio;
    private final String tiempoEstimado;
    private final ZonedDateTime fechaAceptacion;
    private final ZonedDateTime occurredOn;

    public CotizacionAceptadaEvent(UUID idCotizacion, UUID idServicio, UUID idUsuarioTecnico, 
                                   UUID idCliente, BigDecimal precio, String tiempoEstimado, 
                                   ZonedDateTime fechaAceptacion) {
        this.idCotizacion = idCotizacion;
        this.idServicio = idServicio;
        this.idUsuarioTecnico = idUsuarioTecnico;
        this.idCliente = idCliente;
        this.precio = precio;
        this.tiempoEstimado = tiempoEstimado;
        this.fechaAceptacion = fechaAceptacion;
        this.occurredOn = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public UUID getIdCotizacion() {
        return idCotizacion;
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public UUID getIdUsuarioTecnico() {
        return idUsuarioTecnico;
    }

    public UUID getIdCliente() {
        return idCliente;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public ZonedDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }

    public ZonedDateTime getOccurredOn() {
        return occurredOn;
    }
}
