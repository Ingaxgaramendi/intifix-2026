package com.intifix.modules.payments.exception;

public class ReembolsoNoPermitidoException extends RuntimeException {

    public ReembolsoNoPermitidoException(String message) {
        super(message);
    }

    public ReembolsoNoPermitidoException() {
        super("El pago no puede ser reembolsado en su estado actual");
    }
}
