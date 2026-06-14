package com.intifix.modules.chat.exception;

import java.util.UUID;

public class MensajeNoEncontradoException extends ChatException {

    private static final String CODIGO = "MENSAJE_NO_ENCONTRADO";

    public MensajeNoEncontradoException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static MensajeNoEncontradoException porId(UUID id) {
        return new MensajeNoEncontradoException("Mensaje no encontrado: " + id);
    }
}
