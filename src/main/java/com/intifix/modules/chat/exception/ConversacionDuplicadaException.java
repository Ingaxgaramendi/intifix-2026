package com.intifix.modules.chat.exception;

import java.util.UUID;

/**
 * Se lanza al intentar crear una segunda conversación para un servicio que ya
 * la tiene (regla: una conversación por servicio). Se mapea a 409.
 */
public class ConversacionDuplicadaException extends ChatException {

    private static final String CODIGO = "CONVERSACION_DUPLICADA";

    public ConversacionDuplicadaException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static ConversacionDuplicadaException porServicio(UUID idServicio) {
        return new ConversacionDuplicadaException("Ya existe una conversación para el servicio: " + idServicio);
    }
}
