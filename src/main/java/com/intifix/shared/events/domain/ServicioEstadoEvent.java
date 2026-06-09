package com.intifix.shared.events.domain;

import java.util.UUID;

public record ServicioEstadoEvent(
    UUID idServicio,
    String nuevoEstado,
    UUID idCliente,
    UUID idTecnico
) {}
