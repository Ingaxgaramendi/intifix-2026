package com.intifix.modules.chat.exception;

public class ArchivoInvalidoException extends ChatException {

    private static final String CODIGO = "ARCHIVO_INVALIDO";

    public ArchivoInvalidoException(String mensaje) {
        super(CODIGO, mensaje);
    }
}
