package com.intifix.modules.payments.gateway;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {

    boolean existsById(UUID idUsuario);

    Optional<UserInfo> findById(UUID idUsuario);

    record UserInfo(
            UUID idUsuario,
            String nombre,
            String email,
            String tipoDocumento,
            String numeroDocumento
    ) {}
}
