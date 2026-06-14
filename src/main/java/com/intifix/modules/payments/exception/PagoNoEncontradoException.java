package com.intifix.modules.payments.exception;

import java.util.UUID;

public class PagoNoEncontradoException extends RuntimeException {

    public PagoNoEncontradoException(UUID idPago) {
        super("Pago no encontrado con ID: " + idPago);
    }

    public PagoNoEncontradoException(String message) {
        super(message);
    }
}
