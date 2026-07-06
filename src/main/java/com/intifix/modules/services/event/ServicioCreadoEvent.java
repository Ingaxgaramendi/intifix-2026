package com.intifix.modules.services.event;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Domain event published when a new service is created.
 * 
 * This event is used to notify other modules about the creation of a service,
 * enabling cross-module coordination without direct dependencies.
 * 
 * In a future microservices architecture, this event would be published to Kafka
 * for asynchronous processing by interested consumers.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class ServicioCreadoEvent {

    private final UUID idServicio;
    private final UUID idCliente;
    private final UUID idUbicacion;
    private final String titulo;
    private final ZonedDateTime fechaCreacion;
    private final ZonedDateTime occurredOn;

    public ServicioCreadoEvent(UUID idServicio, UUID idCliente, UUID idUbicacion, String titulo, ZonedDateTime fechaCreacion) {
        this.idServicio = idServicio;
        this.idCliente = idCliente;
        this.idUbicacion = idUbicacion;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
        this.occurredOn = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public UUID getIdServicio() {
        return idServicio;
    }

    public UUID getIdCliente() {
        return idCliente;
    }

    public UUID getIdUbicacion() {
        return idUbicacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public ZonedDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public ZonedDateTime getOccurredOn() {
        return occurredOn;
    }
}
