-- Tipo de solicitud: PUBLICA (marketplace abierto) o DIRECTA (técnico específico)
CREATE TYPE tipo_solicitud AS ENUM ('PUBLICA', 'DIRECTA');

ALTER TABLE servicios
    ADD COLUMN tipo_solicitud tipo_solicitud NOT NULL DEFAULT 'PUBLICA',
    ADD COLUMN id_tecnico_directo UUID NULL;

CREATE INDEX idx_servicios_tipo_solicitud ON servicios(tipo_solicitud);
CREATE INDEX idx_servicios_tecnico_directo ON servicios(id_tecnico_directo);
