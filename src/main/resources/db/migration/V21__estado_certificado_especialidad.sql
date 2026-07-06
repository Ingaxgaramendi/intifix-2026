-- Agrega estado de aprobación individual por certificado de especialidad.
-- PENDIENTE: recién subido, esperando revisión admin.
-- APROBADO: certificado verificado; el técnico puede cotizar en esa especialidad.
-- RECHAZADO: certificado inválido; debe volver a subirlo.
ALTER TABLE tecnico_especialidad
    ADD COLUMN estado_certificado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE';
