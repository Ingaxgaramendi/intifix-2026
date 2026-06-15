package com.intifix.modules.audit.entity;

/**
 * Módulo de origen de un evento de auditoría. Permite filtrar la traza por
 * dominio funcional (p. ej. todos los eventos de pagos).
 */
public enum AuditModule {
    AUTH,
    USERS,
    TECHNICIANS,
    SERVICES,
    PAYMENTS,
    CHAT,
    GEO
}
