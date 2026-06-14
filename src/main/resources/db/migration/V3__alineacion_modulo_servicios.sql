-- ============================================================================
-- Alineación del módulo services con el esquema productivo.
-- Aditiva e idempotente: agrega columnas que el código de negocio usa
-- activamente (historial de estados, expiración de cotizaciones, tracking de
-- asignaciones, rúbrica de calificaciones, workflow de reportes).
-- No elimina ni renombra nada existente.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- servicios
-- ----------------------------------------------------------------------------
-- fecha_programada quedó como TIMESTAMP (sin zona) en el esquema original;
-- el resto del esquema usa TIMESTAMPTZ. Se normaliza una sola vez.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'servicios'
          AND column_name = 'fecha_programada'
          AND data_type = 'timestamp without time zone'
    ) THEN
        ALTER TABLE servicios
            ALTER COLUMN fecha_programada TYPE TIMESTAMPTZ
            USING fecha_programada AT TIME ZONE 'UTC';
    END IF;
END
$$;

ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMPTZ;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_finalizacion TIMESTAMPTZ;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS motivo_cancelacion TEXT;

-- ----------------------------------------------------------------------------
-- cotizaciones
-- ----------------------------------------------------------------------------
ALTER TABLE cotizaciones ADD COLUMN IF NOT EXISTS fecha_expiracion TIMESTAMPTZ;
ALTER TABLE cotizaciones ADD COLUMN IF NOT EXISTS motivo_rechazo TEXT;

-- ----------------------------------------------------------------------------
-- asignaciones_servicio
-- ----------------------------------------------------------------------------
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS estado_servicio estado_servicio NOT NULL DEFAULT 'ASIGNADO';
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS fecha_inicio_estimada TIMESTAMPTZ;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS fecha_inicio_real TIMESTAMPTZ;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS fecha_fin_estimada TIMESTAMPTZ;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS fecha_fin_real TIMESTAMPTZ;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS notas_tecnico TEXT;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS notas_cliente TEXT;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS coordenada_encuentro_lat DOUBLE PRECISION;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS coordenada_encuentro_lng DOUBLE PRECISION;
ALTER TABLE asignaciones_servicio ADD COLUMN IF NOT EXISTS direccion_encuentro TEXT;

-- ----------------------------------------------------------------------------
-- historial_servicio (auditoría de transiciones: estado = estado nuevo)
-- ----------------------------------------------------------------------------
ALTER TABLE historial_servicio ADD COLUMN IF NOT EXISTS estado_anterior estado_servicio;
ALTER TABLE historial_servicio ADD COLUMN IF NOT EXISTS cambiado_por UUID;
ALTER TABLE historial_servicio ADD COLUMN IF NOT EXISTS rol_cambiado_por VARCHAR(50);
ALTER TABLE historial_servicio ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
ALTER TABLE historial_servicio ADD COLUMN IF NOT EXISTS user_agent TEXT;

-- ----------------------------------------------------------------------------
-- evidencias_servicio
-- ----------------------------------------------------------------------------
ALTER TABLE evidencias_servicio ADD COLUMN IF NOT EXISTS nombre_archivo VARCHAR(255);
ALTER TABLE evidencias_servicio ADD COLUMN IF NOT EXISTS tamano_bytes BIGINT;
ALTER TABLE evidencias_servicio ADD COLUMN IF NOT EXISTS metadatos JSONB;

-- ----------------------------------------------------------------------------
-- calificaciones (rúbrica detallada opcional)
-- ----------------------------------------------------------------------------
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS puntualidad INTEGER CHECK (puntualidad BETWEEN 1 AND 5);
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS profesionalismo INTEGER CHECK (profesionalismo BETWEEN 1 AND 5);
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS calidad_trabajo INTEGER CHECK (calidad_trabajo BETWEEN 1 AND 5);
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS comunicacion INTEGER CHECK (comunicacion BETWEEN 1 AND 5);
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS recomendaria BOOLEAN;
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMPTZ;
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS aspectos_positivos TEXT[];
ALTER TABLE calificaciones ADD COLUMN IF NOT EXISTS aspectos_mejorar TEXT[];

-- ----------------------------------------------------------------------------
-- reportes (workflow de moderación)
-- ----------------------------------------------------------------------------
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS id_reportado UUID;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS tipo_reporte VARCHAR(50);
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS descripcion_detallada TEXT;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS prioridad VARCHAR(20);
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS resolucion TEXT;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS accion_tomada TEXT;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS resuelto_por UUID;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS fecha_resolucion TIMESTAMPTZ;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMPTZ;
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS evidencias_url TEXT[];
ALTER TABLE reportes ADD COLUMN IF NOT EXISTS metadatos JSONB;

-- ----------------------------------------------------------------------------
-- perfiles_tecnico (búsqueda de técnicos por ubicación, usada por el módulo)
-- ----------------------------------------------------------------------------
ALTER TABLE perfiles_tecnico ADD COLUMN IF NOT EXISTS id_ubicacion UUID REFERENCES ubicaciones(id_ubicacion);
CREATE INDEX IF NOT EXISTS idx_tecnicos_ubicacion ON perfiles_tecnico(id_ubicacion) WHERE id_ubicacion IS NOT NULL;
