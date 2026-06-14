package com.intifix.modules.payments.gateway;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ServiceGateway {

    boolean existsById(UUID idServicio);

    Optional<ServiceInfo> findById(UUID idServicio);

    boolean isPayable(UUID idServicio);

    /**
     * Información del servicio relevante para pagos. El {@code montoAcordado}
     * proviene de la cotización aceptada ligada a la asignación (fuente
     * autoritativa del precio); es {@code null} si el servicio aún no tiene
     * técnico asignado.
     */
    record ServiceInfo(
            UUID idServicio,
            UUID idCliente,
            UUID idTecnico,
            BigDecimal montoAcordado,
            String estado
    ) {}
}
