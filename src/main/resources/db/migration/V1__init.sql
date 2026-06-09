-- ============================================================================
-- INTIFIX v1: Core domain schema (PostgreSQL 14+)
-- ============================================================================

-- Enums / Types
CREATE TYPE rol_usuario AS ENUM ('CLIENTE', 'TECNICO', 'ADMIN');
CREATE TYPE estado_usuario AS ENUM ('ACTIVO', 'SUSPENDIDO', 'ELIMINADO');
CREATE TYPE estado_servicio AS ENUM ('PENDIENTE', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO', 'RECHAZADO');
CREATE TYPE estado_cotizacion AS ENUM ('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'EXPIRADA', 'CANCELADA');
CREATE TYPE estado_pago AS ENUM ('PENDIENTE', 'PROCESANDO', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO', 'CANCELADO');
CREATE TYPE estado_factura AS ENUM ('BORRADOR', 'EMITIDA', 'PAGADA', 'ANULADA', 'VENCIDA');
CREATE TYPE tipo_reporte AS ENUM ('USUARIO', 'SERVICIO', 'PAGO', 'CONDUCTA', 'OTRO');
CREATE TYPE estado_reporte AS ENUM ('ABIERTO', 'EN_INVESTIGACION', 'RESUELTO', 'CERRADO', 'DESESTIMADO');
CREATE TYPE especialidad_tecnico AS ENUM ('ELECTRICISTA', 'PLOMERIA', 'MECANICA', 'CARPINTERIA', 'LIMPIZA', 'OTROS');
CREATE TYPE categoria_diagnostico AS ENUM ('MENOR', 'MEDIO', 'MAYOR', 'CRITICO');
CREATE TYPE mensaje_type AS ENUM ('TEXTO', 'IMAGEN', 'VIDEO', 'UBICACION', 'ARCHIVO', 'NOTIFICACION');
CREATE TYPE mensaje_status AS ENUM ('ENVIADO', 'ENTREGADO', 'LEIDO', 'FALLIDO');

-- ============================================================================
-- USUARIOS (Core user aggregate)
-- ============================================================================
CREATE TABLE usuarios (
    id_usuario              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    correo                  VARCHAR(255) NOT NULL UNIQUE,
    dni                     VARCHAR(20) NOT NULL UNIQUE,
    password_hash           TEXT NOT NULL,
    telefono                VARCHAR(20) NOT NULL UNIQUE,
    rol                     rol_usuario NOT NULL,
    estado                  estado_usuario DEFAULT 'ACTIVO',
    verificado              BOOLEAN DEFAULT false,
    intentos_fallidos       INTEGER DEFAULT 0 CHECK (intentos_fallidos >= 0),
    ultimo_login            TIMESTAMP WITH TIME ZONE,
    fecha_ultimo_cambio_pwd TIMESTAMP WITH TIME ZONE,
    dos_factor_enabled      BOOLEAN DEFAULT false,
    dos_factor_secret       VARCHAR(255),
    ip_registro             INET,
    user_agent_registro     TEXT,
    metadata                JSONB,
    fecha_registro          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_usuarios_correo ON usuarios(correo);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_deleted_at ON usuarios(deleted_at);

-- ============================================================================
-- PERFILES DE USUARIO
-- ============================================================================
CREATE TABLE perfiles_usuario (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id              UUID NOT NULL UNIQUE REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    nombre                  VARCHAR(120) NOT NULL,
    apellido                VARCHAR(120) NOT NULL,
    telefono_contacto       VARCHAR(30),
    avatar_url              VARCHAR(500),
    biografia               TEXT,
    idioma                  VARCHAR(10) DEFAULT 'es',
    timezone                VARCHAR(64) DEFAULT 'UTC',
    coordenada_lat          DOUBLE PRECISION,
    coordenada_lng          DOUBLE PRECISION,
    ubicacion_nombre        VARCHAR(255),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_perfiles_usuario_ubicacion ON perfiles_usuario(coordenada_lat, coordenada_lng);

-- ============================================================================
-- PERFILES TÉCNICO
-- ============================================================================
CREATE TABLE perfiles_tecnico (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id              UUID NOT NULL UNIQUE REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    especialidad            especialidad_tecnico NOT NULL,
    anos_experiencia        INTEGER NOT NULL CHECK (anos_experiencia >= 0),
    bio                     VARCHAR(1000),
    certificaciones         JSONB DEFAULT '[]'::jsonb,
    calificacion_promedio   NUMERIC(3, 2) DEFAULT 0.00,
    total_servicios         INTEGER DEFAULT 0,
    total_resenas           INTEGER DEFAULT 0,
    verificado              BOOLEAN DEFAULT false,
    activo                  BOOLEAN DEFAULT true,
    disponibilidad_estado   VARCHAR(50) DEFAULT 'DISPONIBLE',
    radio_km                INTEGER DEFAULT 10 NOT NULL,
    telefonos_contacto      TEXT[],
    horario_inicio          TIME,
    horario_fin             TIME,
    dias_laborales          INTEGER[] DEFAULT ARRAY[1, 2, 3, 4, 5],
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_perfiles_tecnico_especialidad ON perfiles_tecnico(especialidad);
CREATE INDEX idx_perfiles_tecnico_verificado ON perfiles_tecnico(verificado);

-- ============================================================================
-- REPUTACIÓN TÉCNICO
-- ============================================================================
CREATE TABLE reputacion_tecnico (
    id_usuario_tecnico      UUID PRIMARY KEY REFERENCES perfiles_tecnico(usuario_id) ON DELETE CASCADE,
    promedio                NUMERIC(3, 2) NOT NULL DEFAULT 0.00,
    total_resenas           INTEGER NOT NULL DEFAULT 0,
    total_servicios         INTEGER NOT NULL DEFAULT 0,
    cancelados_cliente      INTEGER DEFAULT 0,
    cancelados_tecnico      INTEGER DEFAULT 0,
    no_presentes            INTEGER DEFAULT 0,
    actualizado_en          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- SERVICIOS (Requests)
-- ============================================================================
CREATE TABLE servicios (
    id_servicio             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cliente              UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    titulo                  VARCHAR(255) NOT NULL,
    descripcion             TEXT NOT NULL,
    categoria_principal     VARCHAR(100),
    especialidad_requerida  especialidad_tecnico,
    duracion_estimada_min   INTEGER,
    presupuesto_estimado    NUMERIC(12, 2),
    coordenada_lat          DOUBLE PRECISION,
    coordenada_lng          DOUBLE PRECISION,
    ubicacion_nombre        VARCHAR(255),
    imagenes                TEXT[] DEFAULT '{}',
    fotos_count             INTEGER DEFAULT 0,
    estado                  estado_servicio DEFAULT 'PENDIENTE',
    prioridad               VARCHAR(20) DEFAULT 'NORMAL',
    urgencia_nivel          INTEGER DEFAULT 1,
    fecha_solicitud_para    TIMESTAMP WITH TIME ZONE,
    notas_privadas          TEXT,
    metadata                JSONB,
    fecha_creacion          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_servicios_cliente ON servicios(id_cliente);
CREATE INDEX idx_servicios_estado ON servicios(estado);
CREATE INDEX idx_servicios_ubicacion ON servicios(coordenada_lat, coordenada_lng);
CREATE INDEX idx_servicios_especialidad ON servicios(especialidad_requerida);

-- ============================================================================
-- HISTORIAL SERVICIO
-- ============================================================================
CREATE TABLE historial_servicio (
    id_historial            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_servicio             UUID NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    estado                  estado_servicio NOT NULL,
    comentario              TEXT,
    usuario_cambio          UUID REFERENCES usuarios(id_usuario),
    fecha_cambio            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_historial_servicio ON historial_servicio(id_servicio);

-- ============================================================================
-- COTIZACIONES
-- ============================================================================
CREATE TABLE cotizaciones (
    id_cotizacion           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_servicio             UUID NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    id_usuario_tecnico      UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    precio                  NUMERIC(12, 2) NOT NULL,
    mensaje                 VARCHAR(1000),
    tiempo_respuesta_min    INTEGER,
    validez_horas           INTEGER DEFAULT 24,
    aceptada_en             TIMESTAMP WITH TIME ZONE,
    rechazada_en            TIMESTAMP WITH TIME ZONE,
    razon_rechazo           TEXT,
    estado                  estado_cotizacion DEFAULT 'PENDIENTE',
    metadata                JSONB,
    fecha_envio             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_cotizaciones_servicio ON cotizaciones(id_servicio);
CREATE INDEX idx_cotizaciones_tecnico ON cotizaciones(id_usuario_tecnico);
CREATE INDEX idx_cotizaciones_estado ON cotizaciones(estado);

-- ============================================================================
-- ASIGNACIONES SERVICIO
-- ============================================================================
CREATE TABLE asignaciones_servicio (
    id_asignacion           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_servicio             UUID NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    id_usuario_tecnico      UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    id_cotizacion           UUID UNIQUE REFERENCES cotizaciones(id_cotizacion),
    fecha_inicio            TIMESTAMP WITH TIME ZONE,
    fecha_fin_estimada      TIMESTAMP WITH TIME ZONE,
    fecha_completacion      TIMESTAMP WITH TIME ZONE,
    estado                  estado_servicio DEFAULT 'EN_PROGRESO',
    notas_tecnico           TEXT,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_asignaciones_servicio ON asignaciones_servicio(id_servicio);
CREATE INDEX idx_asignaciones_tecnico ON asignaciones_servicio(id_usuario_tecnico);

-- ============================================================================
-- CALIFICACIONES
-- ============================================================================
CREATE TABLE calificaciones (
    id_calificacion         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_servicio             UUID NOT NULL UNIQUE REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    id_cliente              UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    id_usuario_tecnico      UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    puntuacion              SMALLINT NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario              VARCHAR(1000),
    aspectos_positivos      TEXT[] DEFAULT '{}',
    aspectos_negativos      TEXT[] DEFAULT '{}',
    puntualidad_rating      SMALLINT CHECK (puntualidad_rating IS NULL OR puntualidad_rating BETWEEN 1 AND 5),
    profesionalismo_rating  SMALLINT CHECK (profesionalismo_rating IS NULL OR profesionalismo_rating BETWEEN 1 AND 5),
    responde_preguntas      BOOLEAN DEFAULT true,
    fecha                   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_calificaciones_servicio ON calificaciones(id_servicio);
CREATE INDEX idx_calificaciones_tecnico ON calificaciones(id_usuario_tecnico);

-- ============================================================================
-- UBICACIONES (Temporary, referenced by live tech locations in Mongo)
-- ============================================================================
CREATE TABLE ubicaciones (
    id_ubicacion            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_tecnico_id      UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    coordenada_lat          DOUBLE PRECISION NOT NULL,
    coordenada_lng          DOUBLE PRECISION NOT NULL,
    exactitud_metros        NUMERIC(8, 2),
    timestamp_ubicacion     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ubicaciones_ubicacion ON ubicaciones(coordenada_lat, coordenada_lng);
CREATE INDEX idx_ubicaciones_tecnico ON ubicaciones(usuario_tecnico_id);

-- ============================================================================
-- DIAGNÓSTICOS IA
-- ============================================================================
CREATE TABLE diagnosticos_ia (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    servicio_id             UUID NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    categoria               categoria_diagnostico NOT NULL,
    confianza               NUMERIC(5, 2) NOT NULL CHECK (confianza BETWEEN 0.00 AND 100.00),
    resumen                 VARCHAR(2000) NOT NULL,
    detalles_tecnico        JSONB,
    modelo                  VARCHAR(80) DEFAULT 'intifix-mock-v1',
    version_modelo          VARCHAR(50),
    latencia_ms             INTEGER,
    metadata                JSONB,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_diagnosticos_servicio ON diagnosticos_ia(servicio_id);

-- ============================================================================
-- SUGERENCIAS ESPECIALIDAD
-- ============================================================================
CREATE TABLE sugerencias_especialidad (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    diagnostico_id          UUID NOT NULL REFERENCES diagnosticos_ia(id) ON DELETE CASCADE,
    especialidad            especialidad_tecnico NOT NULL,
    score                   NUMERIC(5, 2) NOT NULL CHECK (score BETWEEN 0.00 AND 100.00),
    razon                   TEXT,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_sugerencias_diagnostico ON sugerencias_especialidad(diagnostico_id);

-- ============================================================================
-- PAGOS
-- ============================================================================
CREATE TABLE pagos (
    id_pago                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_servicio             UUID NOT NULL REFERENCES servicios(id_servicio) ON DELETE RESTRICT,
    id_metodo_pago          UUID,
    monto                   NUMERIC(12, 2) NOT NULL CHECK (monto > 0),
    estado                  estado_pago DEFAULT 'PENDIENTE',
    transaction_id          VARCHAR(255),
    referencia              VARCHAR(255),
    gateway_respuesta       JSONB,
    metodo_pago_tipo        VARCHAR(50),
    cliente_id              UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    payee_id                UUID REFERENCES usuarios(id_usuario),
    comision_plataforma     NUMERIC(12, 2) DEFAULT 0,
    neto_pagado             NUMERIC(12, 2),
    motivo_fallo            TEXT,
    reintentos_fallidos     INTEGER DEFAULT 0,
    fecha_pago              TIMESTAMP WITH TIME ZONE,
    fecha_vencimiento       TIMESTAMP WITH TIME ZONE,
    reembolso_fecha         TIMESTAMP WITH TIME ZONE,
    reembolso_monto         NUMERIC(12, 2),
    reembolso_razon         TEXT,
    refund_reason_code      VARCHAR(100),
    metadata                JSONB,
    fecha_creacion          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_pagos_servicio ON pagos(id_servicio);
CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_pagos_cliente ON pagos(cliente_id);
CREATE INDEX idx_pagos_referencia ON pagos(referencia);

-- ============================================================================
-- FACTURAS
-- ============================================================================
CREATE TABLE facturas (
    id_factura              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_pago                 UUID NOT NULL UNIQUE REFERENCES pagos(id_pago) ON DELETE CASCADE,
    codigo_factura          VARCHAR(100) NOT NULL UNIQUE,
    numero                  VARCHAR(100) NOT NULL UNIQUE,
    estado_factura          estado_factura DEFAULT 'BORRADOR',
    cliente_id              UUID NOT NULL REFERENCES usuarios(id_usuario),
    proveedor_id            UUID REFERENCES usuarios(id_usuario),
    subtotal                NUMERIC(12, 2) NOT NULL,
    impuesto                NUMERIC(12, 2) DEFAULT 0,
    total                   NUMERIC(12, 2) NOT NULL,
    descripcion_items       TEXT,
    condiciones_pago        VARCHAR(500),
    fecha_pago_esperada     TIMESTAMP WITH TIME ZONE,
    url_pdf                 VARCHAR(500),
    html_factura            TEXT,
    xml_factura             TEXT,
    url_electronica         VARCHAR(500),
    numero_electronico      VARCHAR(100),
    fecha_emision           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    fecha_vencimiento       TIMESTAMP WITH TIME ZONE,
    fecha_anulacion         TIMESTAMP WITH TIME ZONE,
    razon_anulacion         TEXT,
    metadata                JSONB,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_facturas_pago ON facturas(id_pago);
CREATE INDEX idx_facturas_numero ON facturas(numero);
CREATE INDEX idx_facturas_estado ON facturas(estado_factura);

-- ============================================================================
-- REPORTES (Moderation)
-- ============================================================================
CREATE TABLE reportes (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reportante_id           UUID NOT NULL REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    reportado_id            UUID REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    servicio_id             UUID REFERENCES servicios(id_servicio) ON DELETE SET NULL,
    tipo                    tipo_reporte NOT NULL,
    motivo                  VARCHAR(500) NOT NULL,
    descripcion_detallada   TEXT,
    evidencia_url           TEXT[],
    estado                  estado_reporte DEFAULT 'ABIERTO',
    prioridad               VARCHAR(20) DEFAULT 'NORMAL',
    resolucion              VARCHAR(1000),
    accion_tomada           VARCHAR(100),
    resuelto_por            UUID REFERENCES usuarios(id_usuario),
    fecha_resolucion        TIMESTAMP WITH TIME ZONE,
    metadata                JSONB,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_reportes_estado ON reportes(estado);
CREATE INDEX idx_reportes_reportante ON reportes(reportante_id);

-- ============================================================================
-- GRANTS & SEQUENCES
-- ============================================================================
ALTER TABLE usuarios OWNER TO intifix_app;
ALTER TABLE perfiles_usuario OWNER TO intifix_app;
ALTER TABLE perfiles_tecnico OWNER TO intifix_app;
ALTER TABLE reputacion_tecnico OWNER TO intifix_app;
ALTER TABLE servicios OWNER TO intifix_app;
ALTER TABLE historial_servicio OWNER TO intifix_app;
ALTER TABLE cotizaciones OWNER TO intifix_app;
ALTER TABLE asignaciones_servicio OWNER TO intifix_app;
ALTER TABLE calificaciones OWNER TO intifix_app;
ALTER TABLE ubicaciones OWNER TO intifix_app;
ALTER TABLE diagnosticos_ia OWNER TO intifix_app;
ALTER TABLE sugerencias_especialidad OWNER TO intifix_app;
ALTER TABLE pagos OWNER TO intifix_app;
ALTER TABLE facturas OWNER TO intifix_app;
ALTER TABLE reportes OWNER TO intifix_app;
