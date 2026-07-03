package com.intifix.modules.services.enums;

public enum TipoFecha {
    /** Client needs the service as soon as possible; system finds nearest available technician. */
    URGENTE,
    /** Client specifies an exact date and time. */
    EXACTA,
    /** Client gives a date range (max 5 days); technician picks the date+time within it. */
    RANGO
}
