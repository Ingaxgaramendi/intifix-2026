package com.intifix.modules.payments.exception;

public class EstadoPagoInvalidoException extends RuntimeException {

    public EstadoPagoInvalidoException(String message) {
        super(message);
    }

    public EstadoPagoInvalidoException(String estadoActual, String transicionRequerida) {
        super(String.format("No se puede realizar la transición del estado %s a %s", estadoActual, transicionRequerida));
    }
}
