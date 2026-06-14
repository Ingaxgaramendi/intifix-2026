package com.intifix.modules.chat.exception;

public class ConversacionBloqueadaException extends ChatException {

    private static final String CODIGO = "CONVERSACION_BLOQUEADA";

    public ConversacionBloqueadaException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static ConversacionBloqueadaException porDefecto() {
        return new ConversacionBloqueadaException("La conversación está bloqueada");
    }
}
