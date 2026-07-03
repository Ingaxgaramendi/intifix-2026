-- ============================================================================
-- V10: Campos editables del perfil público del técnico.
--  - descripcion: bio / "Acerca de mí" (texto libre).
--  - telefono_contacto: número de contacto público OPCIONAL (distinto del
--    teléfono de login en `usuarios`, por privacidad: el técnico decide publicarlo).
-- La dirección del taller ya se modela con perfiles_tecnico.id_ubicacion (V1).
-- ============================================================================
ALTER TABLE perfiles_tecnico
   ADD COLUMN descripcion TEXT,
   ADD COLUMN telefono_contacto VARCHAR(20);
