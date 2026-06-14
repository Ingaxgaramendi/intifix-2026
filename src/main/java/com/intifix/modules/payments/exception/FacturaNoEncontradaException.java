package com.intifix.modules.payments.exception;

import java.util.UUID;

public class FacturaNoEncontradaException extends RuntimeException {

    public FacturaNoEncontradaException(UUID idFactura) {
        super("Factura no encontrada con ID: " + idFactura);
    }

    public FacturaNoEncontradaException(String message) {
        super(message);
    }
}
