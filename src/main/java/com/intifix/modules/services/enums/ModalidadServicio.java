package com.intifix.modules.services.enums;

/**
 * Espejo exacto del tipo PostgreSQL {@code modalidad_servicio}.
 * Los nombres de las constantes DEBEN coincidir con los labels del enum en BD.
 */
public enum ModalidadServicio {

    /** El técnico se traslada al domicilio del cliente. */
    EN_CASA_CLIENTE,

    /** El cliente lleva el equipo al taller del técnico. */
    EN_TALLER_TECNICO;
}
