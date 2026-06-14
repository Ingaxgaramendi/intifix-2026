package com.intifix.modules.chat.gateway;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida hacia datos que viven fuera del módulo chat (PostgreSQL).
 * Mantiene chat desacoplado de services/users: cero relaciones JPA cruzadas,
 * lo que habilita extraerlo a un microservicio cambiando solo el adaptador.
 */
public interface ChatGateway {

    boolean existeUsuario(UUID idUsuario);

    /**
     * Cliente y técnico asociados a un servicio. El técnico es null si el
     * servicio aún no tiene asignación.
     */
    Optional<ServicioParticipantes> obtenerParticipantes(UUID idServicio);

    record ServicioParticipantes(UUID idServicio, UUID idCliente, UUID idTecnico) {}
}
