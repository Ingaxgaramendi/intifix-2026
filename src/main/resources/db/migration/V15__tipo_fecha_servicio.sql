-- V15: Scheduling mode for service requests
-- Adds three scheduling modes: URGENTE (ASAP), EXACTA (exact date+time), RANGO (date range).

CREATE TYPE tipo_fecha AS ENUM ('URGENTE', 'EXACTA', 'RANGO');

-- Allow NULL so URGENTE services have no fixed date.
ALTER TABLE servicios ALTER COLUMN fecha_programada DROP NOT NULL;

ALTER TABLE servicios
    ADD COLUMN tipo_fecha    tipo_fecha  NOT NULL DEFAULT 'EXACTA',
    ADD COLUMN fecha_inicio_rango TIMESTAMPTZ NULL,
    ADD COLUMN fecha_fin_rango    TIMESTAMPTZ NULL;

CREATE INDEX idx_servicios_tipo_fecha ON servicios (tipo_fecha);
