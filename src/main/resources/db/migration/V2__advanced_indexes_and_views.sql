-- ============================================================================
-- INTIFIX v2: Advanced indexes, constraints & MongoDB collection setup
-- ============================================================================

-- ============================================================================
-- PARTIAL INDEXES (Performance optimization)
-- ============================================================================
CREATE INDEX idx_usuarios_activos ON usuarios(id_usuario) WHERE estado = 'ACTIVO' AND deleted_at IS NULL;
CREATE INDEX idx_servicios_pendientes ON servicios(id_cliente, estado) WHERE estado IN ('PENDIENTE', 'EN_PROGRESO');
CREATE INDEX idx_cotizaciones_activas ON cotizaciones(id_servicio) WHERE estado IN ('PENDIENTE', 'ACEPTADA');
CREATE INDEX idx_pagos_incompletos ON pagos(id_servicio) WHERE estado NOT IN ('COMPLETADO', 'REEMBOLSADO');
CREATE INDEX idx_reportes_abiertos ON reportes(id) WHERE estado IN ('ABIERTO', 'EN_INVESTIGACION');

-- ============================================================================
-- COMPOSITE INDEXES (Query optimization)
-- ============================================================================
CREATE INDEX idx_servicios_estado_fecha ON servicios(estado, created_at DESC);
CREATE INDEX idx_cotizaciones_tecnico_estado ON cotizaciones(id_usuario_tecnico, estado);
CREATE INDEX idx_pagos_cliente_estado ON pagos(cliente_id, estado);
CREATE INDEX idx_facturas_cliente_fecha ON facturas(cliente_id, fecha_emision DESC);
CREATE INDEX idx_calificaciones_tecnico_fecha ON calificaciones(id_usuario_tecnico, created_at DESC);
CREATE INDEX idx_perfiles_tecnico_especialidad_activo ON perfiles_tecnico(especialidad, activo, verificado);

-- ============================================================================
-- UNIQUE CONSTRAINTS & VALIDATION
-- ============================================================================
ALTER TABLE cotizaciones ADD CONSTRAINT uq_cotizacion_por_tecnico_servicio 
    UNIQUE NULLS NOT DISTINCT (id_servicio, id_usuario_tecnico) 
    WHERE deleted_at IS NULL;

ALTER TABLE asignaciones_servicio ADD CONSTRAINT uq_asignacion_por_servicio 
    UNIQUE (id_servicio) WHERE deleted_at IS NULL;

-- ============================================================================
-- TRIGGER: Automatic reputacion_tecnico creation
-- ============================================================================
CREATE OR REPLACE FUNCTION crear_reputacion_inicial()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO reputacion_tecnico (id_usuario_tecnico, promedio, total_resenas, total_servicios)
    VALUES (NEW.usuario_id, 0.00, 0, 0)
    ON CONFLICT DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_crear_reputacion ON perfiles_tecnico;
CREATE TRIGGER trigger_crear_reputacion
AFTER INSERT ON perfiles_tecnico
FOR EACH ROW
EXECUTE FUNCTION crear_reputacion_inicial();

-- ============================================================================
-- TRIGGER: Update historial_servicio on estado change
-- ============================================================================
CREATE OR REPLACE FUNCTION registrar_cambio_estado()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estado IS DISTINCT FROM NEW.estado THEN
        INSERT INTO historial_servicio (id_servicio, estado, comentario, usuario_cambio, fecha_cambio)
        VALUES (NEW.id_servicio, NEW.estado, 'Estado cambiado automáticamente', NEW.id_cliente, NOW());
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_historial_estado ON servicios;
CREATE TRIGGER trigger_historial_estado
AFTER UPDATE ON servicios
FOR EACH ROW
EXECUTE FUNCTION registrar_cambio_estado();

-- ============================================================================
-- TRIGGER: Update reputación on new calificación
-- ============================================================================
CREATE OR REPLACE FUNCTION actualizar_reputacion_calificacion()
RETURNS TRIGGER AS $$
DECLARE
    v_promedio NUMERIC(3, 2);
    v_total INTEGER;
BEGIN
    SELECT 
        ROUND(AVG(c.puntuacion)::NUMERIC, 2),
        COUNT(*)
    INTO v_promedio, v_total
    FROM calificaciones c
    WHERE c.id_usuario_tecnico = NEW.id_usuario_tecnico;

    UPDATE reputacion_tecnico
    SET promedio = COALESCE(v_promedio, 0),
        total_resenas = v_total,
        actualizado_en = NOW()
    WHERE id_usuario_tecnico = NEW.id_usuario_tecnico;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_actualizar_reputacion ON calificaciones;
CREATE TRIGGER trigger_actualizar_reputacion
AFTER INSERT ON calificaciones
FOR EACH ROW
EXECUTE FUNCTION actualizar_reputacion_calificacion();

-- ============================================================================
-- TRIGGER: Soft delete on usuarios cascades estado
-- ============================================================================
CREATE OR REPLACE FUNCTION cascada_eliminacion_usuario()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.deleted_at IS NOT NULL AND OLD.deleted_at IS NULL THEN
        UPDATE perfiles_usuario SET updated_at = NOW() WHERE usuario_id = NEW.id_usuario;
        UPDATE perfiles_tecnico SET updated_at = NOW() WHERE usuario_id = NEW.id_usuario;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_cascada_eliminacion ON usuarios;
CREATE TRIGGER trigger_cascada_eliminacion
AFTER UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION cascada_eliminacion_usuario();

-- ============================================================================
-- VIEWS (Common queries for application)
-- ============================================================================

-- Active technicians with reputation
CREATE OR REPLACE VIEW v_tecnicos_activos AS
SELECT 
    pt.id,
    pt.usuario_id,
    u.correo,
    pt.especialidad,
    pt.anos_experiencia,
    pt.verificado,
    pt.activo,
    pt.radio_km,
    pr.promedio AS calificacion,
    pr.total_resenas,
    pr.total_servicios,
    pp.coordenada_lat,
    pp.coordenada_lng
FROM perfiles_tecnico pt
JOIN usuarios u ON pt.usuario_id = u.id_usuario
LEFT JOIN reputacion_tecnico pr ON pt.usuario_id = pr.id_usuario_tecnico
LEFT JOIN perfiles_usuario pp ON u.id_usuario = pp.usuario_id
WHERE u.estado = 'ACTIVO' AND pt.activo = true AND u.deleted_at IS NULL;

-- Services ready for quoting
CREATE OR REPLACE VIEW v_servicios_por_cotizar AS
SELECT 
    s.id_servicio,
    s.id_cliente,
    s.titulo,
    s.especialidad_requerida,
    s.estado,
    s.fecha_creacion,
    COUNT(c.id_cotizacion) AS cotizaciones_recibidas
FROM servicios s
LEFT JOIN cotizaciones c ON s.id_servicio = c.id_servicio AND c.deleted_at IS NULL
WHERE s.estado = 'PENDIENTE' AND s.deleted_at IS NULL
GROUP BY s.id_servicio;

-- Payment summary by service
CREATE OR REPLACE VIEW v_resumen_pago_servicio AS
SELECT 
    p.id_servicio,
    s.titulo,
    p.id_pago,
    p.monto,
    p.estado,
    f.numero AS numero_factura,
    f.estado_factura
FROM pagos p
JOIN servicios s ON p.id_servicio = s.id_servicio
LEFT JOIN facturas f ON p.id_pago = f.id_pago
WHERE p.created_at IS NOT NULL;

-- ============================================================================
-- AUDIT LOG TABLE
-- ============================================================================
CREATE TABLE audit_log (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tabla                   VARCHAR(100),
    operacion               VARCHAR(20),
    usuario_id              UUID REFERENCES usuarios(id_usuario),
    datos_antiguos          JSONB,
    datos_nuevos            JSONB,
    razon_cambio            TEXT,
    ip_address              INET,
    timestamp               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp DESC);
CREATE INDEX idx_audit_log_tabla_usuario ON audit_log(tabla, usuario_id);

-- ============================================================================
-- SESSION STORE TABLE (for JWT refresh tokens if needed)
-- ============================================================================
CREATE TABLE session_store (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id              UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    refresh_token           VARCHAR(500),
    ip_address              INET,
    user_agent              TEXT,
    expira_en               TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_session_usuario ON session_store(usuario_id);
CREATE INDEX idx_session_expiracion ON session_store(expira_en);

-- ============================================================================
-- COMMENT ON COLUMNS (Documentation)
-- ============================================================================
COMMENT ON COLUMN usuarios.dos_factor_enabled IS 'Indica si 2FA está habilitado para la cuenta';
COMMENT ON COLUMN usuarios.metadata IS 'JSON almacenamiento flexible para datos opcionales';
COMMENT ON COLUMN servicios.imagenes IS 'Array de URLs de imágenes del problema';
COMMENT ON COLUMN servicios.presupuesto_estimado IS 'Presupuesto máximo que cliente está dispuesto a pagar';
COMMENT ON COLUMN cotizaciones.validez_horas IS 'Horas hasta que la cotización expira';
COMMENT ON COLUMN cotizaciones.tiempo_respuesta_min IS 'Minutos estimados que tardará el técnico';
COMMENT ON COLUMN pagos.comision_plataforma IS 'Comisión deducida por Intifix del monto total';
COMMENT ON COLUMN pagos.neto_pagado IS 'Monto neto que recibe el técnico después de comisión';
COMMENT ON COLUMN calificaciones.aspectos_positivos IS 'Array de aspectos que fueron bien ejecutados';
COMMENT ON COLUMN diagnosticos_ia.detalles_tecnico IS 'JSON con detalles técnicos del diagnóstico';
