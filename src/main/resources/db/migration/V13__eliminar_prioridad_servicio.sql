-- Eliminar columna prioridad de la tabla servicios.
-- La prioridad no es un campo que el cliente deba gestionar manualmente;
-- la urgencia se comunica en la descripción y fecha programada.

ALTER TABLE servicios DROP COLUMN IF EXISTS prioridad;

DROP TYPE IF EXISTS prioridad_servicio;
