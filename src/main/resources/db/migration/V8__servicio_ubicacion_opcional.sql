-- =============================================================================
-- V8 — Ubicación opcional en servicios
-- Cuando la modalidad es EN_TALLER_TECNICO el servicio se atiende en el taller
-- del técnico, así que el cliente NO registra su ubicación. id_ubicacion deja de
-- ser obligatorio; sigue siendo requerido para EN_CASA_CLIENTE (validado en la app).
-- =============================================================================

ALTER TABLE servicios ALTER COLUMN id_ubicacion DROP NOT NULL;
