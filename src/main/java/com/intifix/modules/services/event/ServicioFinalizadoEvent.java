package com.intifix.modules.services.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Domain event published when a service is completed/finished.
 * 
 * This event is used to notify other modules about the completion of a service,
 * enabling cross-module coordination without direct dependencies.
 * 
 * In a future microservices architecture, this event would be published to Kafka
 * for asynchronous processing by interested consumers (e.g., payments module for final settlement).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioFinalizadoEvent {

    private final UUID idServicio;
    private final UUID idAsignacion;
    private final UUID idUsuarioTecnico;
    private final UUID idCliente;
    private final ZonedDateTime fechaFinalizacion;
    private final ZonedDateTime occurredOn;

    public ServicioFinalizadoEvent(UUID idServicio, UUID idAsignacion, UUID idUsuarioTecnico, 
                                  UUID idCliente, ZonedDateTime fechaFinalizacion) {
        this.idServicio = idServicio;
        this.idAsignacion = idAsignacion;
        this.idUsuarioTecnico = idUsuarioTecnico;
        this.idCliente = idCliente;
        this.fechaFinalizacion = fechaFinalizacion;
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

    public ZonedDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public ZonedDateTime getOccurredOn() {
        return occurredOn;
    }
}
