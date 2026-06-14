package com.intifix.modules.payments.gateway;

import java.util.Optional;
import java.util.UUID;

public interface TechnicianGateway {

    boolean existsById(UUID idTecnico);

    Optional<TechnicianInfo> findById(UUID idTecnico);

    record TechnicianInfo(
            UUID idTecnico,
            String nombre,
            String email,
            String banco,
            String cuentaBancaria
    ) {}
}
