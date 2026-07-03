-- =============================================================================
-- V6 — Foto de perfil del técnico
-- El técnico puede subir su foto de perfil (alojada en Cloudinary). El cliente
-- ya tenía perfiles_cliente.foto_perfil_url; aquí se agrega el equivalente al
-- perfil del técnico.
-- =============================================================================

ALTER TABLE perfiles_tecnico ADD COLUMN IF NOT EXISTS foto_perfil_url TEXT;
