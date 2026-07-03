package com.intifix.modules.services.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Domain event published when a rating is registered for a service.
 * 
 * This event is used to notify other modules about the registration of a rating,
 * enabling cross-module coordination without direct dependencies.
 * 
 * In a future microservices architecture, this event would be published to Kafka
 * for asynchronous processing by interested consumers (e.g., technicians module for reputation update).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public class CalificacionRegistradaEvent {

    private final UUID idCalificacion;
    private final UUID idServicio;
    private final UUID idUsuarioTecnico;
    private final UUID idCliente;
    private final Integer puntuacion;
    private final String comentario;
    private final ZonedDateTime fechaCalificacion;
    private final ZonedDateTime occurredOn;

    public CalificacionRegistradaEvent(UUID idCalificacion, UUID idServicio, UUID idUsuarioTecnico, 
                                      UUID idCliente, Integer puntuacion, String comentario, 
                                      ZonedDateTime fechaCalificacion) {
        this.idCalificacion = idCalificacion;
        this.idServicio = idServicio;
        this.idUsuarioTecnico = idUsuarioTecnico;
        this.idCliente = idCliente;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fechaCalificacion = fechaCalificacion;
        this.occurredOn = ZonedDateTime.now();
    }

    public UUID getIdCalificacion() {
        return idCalificacion;
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

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public ZonedDateTime getFechaCalificacion() {
        return fechaCalificacion;
    }

    public ZonedDateTime getOccurredOn() {
        return occurredOn;
    }
}
