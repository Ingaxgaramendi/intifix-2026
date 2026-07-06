package com.intifix.modules.services.exception;

import java.util.UUID;

public class ClienteNoActivoException extends ServicioException {

    public ClienteNoActivoException(UUID idServicio) {
        super("Este servicio no está disponible actualmente (servicio: " + idServicio + ")", "CLIENTE_NO_ACTIVO");
    }

    public static ClienteNoActivoException forServicio(UUID idServicio) {
        return new ClienteNoActivoException(idServicio);
    }
}
