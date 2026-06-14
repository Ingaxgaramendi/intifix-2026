package com.intifix.modules.payments.exception;

import java.util.UUID;

public class PagoDuplicadoException extends RuntimeException {

    public PagoDuplicadoException(UUID idServicio) {
        super("Ya existe un pago para el servicio con ID: " + idServicio);
    }

    public PagoDuplicadoException(String message) {
        super(message);
    }
}
