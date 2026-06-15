package com.intifix.modules.audit.entity;

/**
 * Acción registrada en {@code websocket_logs}: ciclo de la sesión STOMP y
 * actividad de mensajería en tiempo real.
 */
public enum WebSocketAction {
    CONNECT,
    DISCONNECT,
    MESSAGE_SENT,
    MESSAGE_READ
}
