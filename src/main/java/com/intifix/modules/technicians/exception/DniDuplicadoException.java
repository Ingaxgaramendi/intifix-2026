package com.intifix.modules.technicians.exception;

public class DniDuplicadoException extends TecnicoException {

    public DniDuplicadoException(String message) {
        super(message, "DNI_DUPLICADO");
    }

    public DniDuplicadoException(String message, Throwable cause) {
        super(message, "DNI_DUPLICADO", cause);
    }

    public static DniDuplicadoException byDniRuc(String dniRuc) {
        return new DniDuplicadoException("Ya existe un técnico con el DNI/RUC: " + dniRuc);
    }
}
