package com.intifix.modules.chat.exception;

import java.util.UUID;

/**
 * El servicio referenciado no puede alojar un chat: no existe o aún no tiene
 * técnico asignado (no hay par cliente↔técnico definido). Se mapea a 400.
 */
public class ServicioInvalidoException extends ChatException {

    private static final String CODIGO = "SERVICIO_INVALIDO_PARA_CHAT";

    public ServicioInvalidoException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static ServicioInvalidoException noEncontrado(UUID idServicio) {
        return new ServicioInvalidoException("El servicio no existe: " + idServicio);
    }

    public static ServicioInvalidoException sinTecnico(UUID idServicio) {
        return new ServicioInvalidoException("El servicio aún no tiene técnico asignado: " + idServicio);
    }
}
