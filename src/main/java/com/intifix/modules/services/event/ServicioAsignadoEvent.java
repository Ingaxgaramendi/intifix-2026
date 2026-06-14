package com.intifix.modules.services.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Domain event published when a service is assigned to a technician.
 * 
 * This event is used to notify other modules about the assignment of a service,
 * enabling cross-module coordination without direct dependencies.
 * 
 * In a future microservices architecture, this event would be published to Kafka
 * for asynchronous processing by interested consumers (e.g., notifications module).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioAsignadoEvent {

    private final UUID idServicio;
    private final UUID idAsignacion;
    private final UUID idUsuarioTecnico;
    private final UUID idCliente;
    private final UUID idCotizacion;
    private final ZonedDateTime fechaAsignacion;
    private final ZonedDateTime occurredOn;

    public ServicioAsignadoEvent(UUID idServicio, UUID idAsignacion, UUID idUsuarioTecnico, 
                                UUID idCliente, UUID idCotizacion, ZonedDateTime fechaAsignacion) {
        this.idServicio = idServicio;
        this.idAsignacion = idAsignacion;
        this.idUsuarioTecnico = idUsuarioTecnico;
        this.idCliente = idCliente;
        this.idCotizacion = idCotizacion;
        this.fechaAsignacion = fechaAsignacion;
        this.occurredOn = ZonedDateTime.now();
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public UUID getIdAsignacion() {
        return idAsignacion;
    }

    public UUID getIdUsuarioTecnico() {
        return idUsuarioTecnico;
    }

    public UUID getIdCliente() {
        return idCliente;
    }

    public UUID getIdCotizacion() {
        return idCotizacion;
    }

    public ZonedDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public ZonedDateTime getOccurredOn() {
        return occurredOn;
    }
}
