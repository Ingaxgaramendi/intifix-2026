package com.intifix.modules.payments.exception;

import java.util.UUID;

public class MetodoPagoNoEncontradoException extends RuntimeException {

    public MetodoPagoNoEncontradoException(UUID idMetodoPago) {
        super("Método de pago no encontrado con ID: " + idMetodoPago);
    }

    public MetodoPagoNoEncontradoException(String message) {
        super(message);
    }
}
