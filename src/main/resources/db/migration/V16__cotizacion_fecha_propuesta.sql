-- V16: Technician proposes a specific date/time when quoting.
-- Nullable: only required for URGENTE and RANGO service modes.
ALTER TABLE cotizaciones
    ADD COLUMN fecha_propuesta TIMESTAMPTZ NULL;
