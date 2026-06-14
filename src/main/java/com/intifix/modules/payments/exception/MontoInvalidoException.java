package com.intifix.modules.payments.exception;

public class MontoInvalidoException extends RuntimeException {

    public MontoInvalidoException(String message) {
        super(message);
    }

    public MontoInvalidoException() {
        super("La suma de comisión de plataforma, monto neto del técnico e impuesto total debe ser igual al monto total");
    }
}
