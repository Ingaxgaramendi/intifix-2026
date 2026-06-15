package com.intifix.modules.audit.entity;

/**
 * Acción de negocio capturada en {@code audit_events}. Modela el verbo del
 * evento de forma normalizada para reportería y dashboards.
 */
public enum AuditAction {
    CREAR,
    ACTUALIZAR,
    ELIMINAR,
    APROBAR,
    RECHAZAR,
    CANCELAR,
    ACEPTAR,
    PAGAR,
    EMITIR,
    ENVIAR,
    ACTUALIZAR_UBICACION
}
