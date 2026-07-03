CREATE TYPE tipo_apelacion  AS ENUM ('SUSPENSION', 'BAN');
CREATE TYPE estado_apelacion AS ENUM ('PENDIENTE', 'REVISADA', 'RESUELTA');

CREATE TABLE apelaciones (
    id_apelacion  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    correo        VARCHAR(255)  NOT NULL,
    tipo          tipo_apelacion  NOT NULL,
    mensaje       TEXT          NOT NULL,
    estado        estado_apelacion NOT NULL DEFAULT 'PENDIENTE',
    nota_admin    TEXT,
    fecha_envio   TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_apelaciones_correo ON apelaciones (correo);
CREATE INDEX idx_apelaciones_estado ON apelaciones (estado);
CREATE INDEX idx_apelaciones_fecha  ON apelaciones (fecha_envio DESC);
