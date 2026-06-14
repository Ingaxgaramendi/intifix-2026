package com.intifix.modules.geo.entity;

/**
 * Origen de la ubicación con la que se ubicó a un técnico en una búsqueda:
 * su GPS en vivo (Mongo) o su ubicación pública registrada (PostgreSQL).
 */
public enum FuenteUbicacion {
    LIVE,
    PUBLICA
}
