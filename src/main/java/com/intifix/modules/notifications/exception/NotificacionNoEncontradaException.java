package com.intifix.modules.notifications.exception;

import java.util.UUID;

/**
 * Notificación inexistente o ajena al usuario autenticado. Para no filtrar la
 * existencia de notificaciones de terceros, el acceso ajeno también se traduce
 * a "no encontrada" (404).
 */
public class NotificacionNoEncontradaException extends RuntimeException {

    public NotificacionNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static NotificacionNoEncontradaException porId(UUID id) {
        return new NotificacionNoEncontradaException("Notificación no encontrada: " + id);
    }
}
