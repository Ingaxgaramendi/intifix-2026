-- =============================================================================
-- V7 — Certificado por especialidad del técnico
-- El certificado deja de ser único por técnico y pasa a ser por especialidad:
-- cada especialidad asignada (al registrarse o desde el perfil) lleva su propio
-- certificado (URL en Cloudinary). Se guarda en la tabla puente.
-- =============================================================================

ALTER TABLE tecnico_especialidad ADD COLUMN IF NOT EXISTS certificado_url TEXT;
