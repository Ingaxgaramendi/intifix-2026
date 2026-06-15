package com.intifix.modules.audit.entity;

/**
 * Motivo de un evento de seguridad en {@code security_events}. Cubre el ciclo
 * de autenticación/autorización y los patrones de abuso vigilados.
 */
public enum SecurityReason {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    JWT_INVALID,
    JWT_EXPIRED,
    ACCESS_DENIED,
    BRUTE_FORCE,
    IDOR_ATTEMPT,
    REFRESH_TOKEN_INVALID
}
