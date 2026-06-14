package com.intifix.modules.notifications.entity;

/**
 * Categoría de la notificación. Permite al cliente agrupar/filtrar y elegir
 * ícono/acción al tocarla.
 */
public enum TipoNotificacion {
    MENSAJE_NUEVO,
    MENSAJE_LEIDO,
    CONVERSACION_BLOQUEADA,
    SERVICIO,
    PAGO,
    SISTEMA
}
