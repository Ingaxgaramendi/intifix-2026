package com.intifix.modules.users.exception;

public class DniDuplicadoException extends ClienteException {

    public DniDuplicadoException(String message) {
        super(message, "DNI_DUPLICADO");
    }

    public static DniDuplicadoException byDniRuc(String dniRuc) {
        return new DniDuplicadoException("Ya existe un cliente registrado con el DNI/RUC: " + dniRuc);
    }
}
