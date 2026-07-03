-- =============================================================================
-- V5 — Fotos del servicio
-- El cliente adjunta de 1 a 5 fotos al crear un servicio (alojadas en Cloudinary).
-- Se guardan como arreglo de URLs en la propia tabla servicios para evitar una
-- tabla extra; el límite 1..5 se valida en la capa de aplicación.
-- =============================================================================

ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fotos text[];
