package com.intifix.modules.chat.exception;

import java.util.UUID;

public class ConversacionNoEncontradaException extends ChatException {

    private static final String CODIGO = "CONVERSACION_NO_ENCONTRADA";

    public ConversacionNoEncontradaException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static ConversacionNoEncontradaException porId(UUID id) {
        return new ConversacionNoEncontradaException("Conversación no encontrada: " + id);
    }

    public static ConversacionNoEncontradaException porServicio(UUID idServicio) {
        return new ConversacionNoEncontradaException("No existe conversación para el servicio: " + idServicio);
    }
}
