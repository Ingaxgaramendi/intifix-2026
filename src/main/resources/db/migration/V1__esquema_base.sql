-- ============================================================================
-- INTIFIX - POSTGRESQL ENTERPRISE DATABASE
-- Core Transaccional, Financiero y de Agendamiento - Nivel Nacional
-- Optimized for PostgreSQL 15+ & UUID Native Architecture
--
-- Espejo exacto del esquema productivo. En la BD cloud ya provisionada esta
-- migración se marca como baseline (no se ejecuta); en BDs nuevas crea todo.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- ENUMS (ESTADOS DE NEGOCIO ESTRICTOS)
-- ============================================================================
CREATE TYPE rol_usuario AS ENUM ('CLIENTE', 'TECNICO', 'ADMIN');
CREATE TYPE estado_usuario AS ENUM ('ACTIVO', 'SUSPENDIDO', 'BANEADO');
CREATE TYPE estado_aprobacion_tecnico AS ENUM ('PENDIENTE', 'APROBADO', 'RECHAZADO');
CREATE TYPE disponibilidad_tecnico AS ENUM ('DISPONIBLE', 'OCUPADO');
CREATE TYPE modalidad_servicio AS ENUM ('EN_CASA_CLIENTE', 'EN_TALLER_TECNICO');
CREATE TYPE prioridad_servicio AS ENUM ('BAJA', 'MEDIA', 'ALTA', 'URGENTE');
CREATE TYPE estado_servicio AS ENUM ('PENDIENTE', 'COTIZANDO', 'ASIGNADO', 'EN_PROCESO', 'FINALIZADO', 'CANCELADO');
CREATE TYPE estado_cotizacion AS ENUM ('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'EXPIRADA');
CREATE TYPE tipo_archivo AS ENUM ('IMAGEN', 'VIDEO', 'PDF');
CREATE TYPE estado_reporte AS ENUM ('PENDIENTE', 'EN_REVISION', 'RESUELTO');
CREATE TYPE estado_pago AS ENUM ('PENDIENTE', 'PAGADO', 'REEMBOLSADO', 'FALLIDO');
CREATE TYPE tipo_comprobante AS ENUM ('BOLETA', 'FACTURA', 'NOTA_CREDITO');
CREATE TYPE estado_fiscal_comprobante AS ENUM ('PENDIENTE', 'ENVIADO_PROCESADO', 'RECHAZADO', 'ANULADO');

-- ============================================================================
-- 1. IDENTIDAD Y CONTROL DE ACCESO
-- ============================================================================
CREATE TABLE usuarios (
   id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   correo VARCHAR(255) NOT NULL UNIQUE,
   password_hash TEXT NOT NULL,
   telefono VARCHAR(20) NOT NULL UNIQUE,
   estado estado_usuario NOT NULL DEFAULT 'ACTIVO',
   verificado BOOLEAN NOT NULL DEFAULT FALSE,
   intentos_fallidos INTEGER NOT NULL DEFAULT 0,
   ultimo_login TIMESTAMPTZ,
   fecha_registro TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_usuarios_correo ON usuarios(correo);

CREATE TABLE usuario_roles (
   id_usuario UUID NOT NULL,
   rol rol_usuario NOT NULL,
   PRIMARY KEY (id_usuario, rol),
   FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_roles_rol ON usuario_roles(rol);

-- ============================================================================
-- 2. PERFILES DE USUARIO DESACOPLADOS
-- ============================================================================
CREATE TABLE perfiles_cliente (
   id_usuario UUID PRIMARY KEY,
   nombres_completos VARCHAR(255) NOT NULL,
   dni_ruc VARCHAR(20) UNIQUE,
   foto_perfil_url TEXT,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_clientes_dni ON perfiles_cliente(dni_ruc) WHERE dni_ruc IS NOT NULL;

CREATE TABLE perfiles_tecnico (
   id_usuario UUID PRIMARY KEY,
   nombres_completos VARCHAR(255) NOT NULL,
   dni_ruc VARCHAR(20) NOT NULL UNIQUE,
   experiencia_anios INTEGER NOT NULL DEFAULT 0 CHECK (experiencia_anios >= 0),
   estado_aprobacion estado_aprobacion_tecnico NOT NULL DEFAULT 'PENDIENTE',
   disponibilidad disponibilidad_tecnico NOT NULL DEFAULT 'DISPONIBLE',
   tarifa_base NUMERIC(10,2) NOT NULL DEFAULT 0.00 CHECK (tarifa_base >= 0),
   dni_frontal_url TEXT NOT NULL,
   dni_trasero_url TEXT NOT NULL,
   antecedente_penal_url TEXT NOT NULL,
   certificado_tecnico_url TEXT,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_tecnicos_dni ON perfiles_tecnico(dni_ruc);
CREATE INDEX idx_tecnicos_disponibles ON perfiles_tecnico(disponibilidad) WHERE estado_aprobacion = 'APROBADO';

-- ============================================================================
-- 3. GESTIÓN HORARIA Y DISPONIBILIDAD (Agenda Enterprise)
-- ============================================================================
CREATE TABLE horarios_tecnico (
   id_horario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_usuario_tecnico UUID NOT NULL,
   dia_semana INT NOT NULL CHECK (dia_semana BETWEEN 0 AND 6), -- 0: Domingo, 1: Lunes, etc.
   hora_inicio TIME NOT NULL,
   hora_fin TIME NOT NULL,
   activo BOOLEAN NOT NULL DEFAULT TRUE,
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE,
   CONSTRAINT chk_horas_rango CHECK (hora_inicio < hora_fin),
   CONSTRAINT uq_tecnico_dia_bloque UNIQUE (id_usuario_tecnico, dia_semana, hora_inicio)
);

CREATE INDEX idx_horarios_busqueda_agenda ON horarios_tecnico(dia_semana, hora_inicio, hora_fin) WHERE activo = TRUE;

CREATE TABLE excepciones_horario_tecnico (
   id_excepcion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_usuario_tecnico UUID NOT NULL,
   fecha_inicio TIMESTAMPTZ NOT NULL,
   fecha_fin TIMESTAMPTZ NOT NULL,
   motivo VARCHAR(255),
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE,
   CONSTRAINT chk_fechas_excepcion CHECK (fecha_inicio < fecha_fin)
);

CREATE INDEX idx_excepciones_fechas ON excepciones_horario_tecnico(id_usuario_tecnico, fecha_inicio, fecha_fin);

-- ============================================================================
-- 4. GEOGRAFÍA Y ESPECIALIDADES
-- ============================================================================
CREATE TABLE ubicaciones (
   id_ubicacion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   departamento VARCHAR(100) NOT NULL,
   provincia VARCHAR(100) NOT NULL,
   distrito VARCHAR(100) NOT NULL,
   direccion_texto VARCHAR(255) NOT NULL,
   referencia TEXT,
   latitud DECIMAL(10,7) NOT NULL,
   longitud DECIMAL(10,7) NOT NULL,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ubicaciones_geo ON ubicaciones(latitud, longitud);
CREATE INDEX idx_ubicaciones_ubigeo ON ubicaciones(departamento, provincia, distrito);

CREATE TABLE table_especialidades (
   id_especialidad UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   nombre VARCHAR(150) NOT NULL UNIQUE,
   descripcion TEXT
);

CREATE TABLE tecnico_especialidad (
   id_usuario_tecnico UUID NOT NULL,
   id_especialidad UUID NOT NULL,
   PRIMARY KEY (id_usuario_tecnico, id_especialidad),
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_especialidad) REFERENCES table_especialidades(id_especialidad) ON DELETE CASCADE
);

-- ============================================================================
-- 5. FLUJO OPERATIVO CONTRACTUAL (Servicios y Cotizaciones)
-- ============================================================================
CREATE TABLE servicios (
   id_servicio UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_cliente UUID NOT NULL,
   id_ubicacion UUID NOT NULL,
   titulo VARCHAR(255) NOT NULL,
   descripcion TEXT NOT NULL,
   modalidad modalidad_servicio NOT NULL,
   prioridad prioridad_servicio NOT NULL DEFAULT 'MEDIA',
   estado estado_servicio NOT NULL DEFAULT 'PENDIENTE',
   presupuesto_maximo NUMERIC(10,2) CHECK (presupuesto_maximo > 0),
   fecha_programada TIMESTAMP NOT NULL,
   fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_cliente) REFERENCES perfiles_cliente(id_usuario),
   FOREIGN KEY (id_ubicacion) REFERENCES ubicaciones(id_ubicacion)
);

CREATE INDEX idx_servicios_estado ON servicios(estado);
CREATE INDEX idx_servicios_cliente ON servicios(id_cliente);

CREATE TABLE cotizaciones (
   id_cotizacion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL,
   id_usuario_tecnico UUID NOT NULL,
   precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
   tiempo_estimado VARCHAR(100) NOT NULL,
   estado estado_cotizacion NOT NULL DEFAULT 'PENDIENTE',
   comentario TEXT,
   fecha_envio TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   fecha_respuesta TIMESTAMPTZ,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE
);

CREATE TABLE asignaciones_servicio (
   id_asignacion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL UNIQUE,
   id_usuario_tecnico UUID NOT NULL,
   id_cotizacion UUID NOT NULL UNIQUE,
   fecha_asignacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_cotizacion) REFERENCES cotizaciones(id_cotizacion) ON DELETE CASCADE
);

-- ============================================================================
-- 6. AUDITORÍA OPERATIVA, REPUTACIÓN Y REPORTES
-- ============================================================================
CREATE TABLE evidencias_servicio (
   id_evidencia UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL,
   url_archivo TEXT NOT NULL,
   tipo tipo_archivo NOT NULL,
   descripcion TEXT,
   subido_por UUID NOT NULL,
   fecha_subida TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (subido_por) REFERENCES usuarios(id_usuario)
);

CREATE TABLE historial_servicio (
   id_historial UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL,
   estado estado_servicio NOT NULL,
   comentario TEXT,
   fecha_cambio TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE
);

CREATE TABLE calificaciones (
   id_calificacion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL UNIQUE,
   id_cliente UUID NOT NULL,
   id_usuario_tecnico UUID NOT NULL,
   puntuacion INTEGER NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
   comentario TEXT,
   fecha TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (id_cliente) REFERENCES perfiles_cliente(id_usuario),
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario)
);

CREATE TABLE reputacion_tecnico (
   id_usuario_tecnico UUID PRIMARY KEY,
   promedio NUMERIC(3,2) NOT NULL DEFAULT 0.00 CHECK (promedio BETWEEN 0.00 AND 5.00),
   total_resenas INTEGER NOT NULL DEFAULT 0 CHECK (total_resenas >= 0),
   total_servicios INTEGER NOT NULL DEFAULT 0 CHECK (total_servicios >= 0),
   actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_usuario_tecnico) REFERENCES perfiles_tecnico(id_usuario) ON DELETE CASCADE
);

CREATE TABLE reportes (
   id_reporte UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL,
   id_usuario_reporta UUID NOT NULL,
   motivo TEXT NOT NULL,
   estado estado_reporte NOT NULL DEFAULT 'PENDIENTE',
   fecha TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (id_usuario_reporta) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- 7. MÓDULO FINANCIERO Y COMPLIANCE TRIBUTARIO ELECTRÓNICO
-- ============================================================================
CREATE TABLE metodo_pago (
   id_metodo_pago UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE pagos (
   id_pago UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_servicio UUID NOT NULL UNIQUE,
   id_metodo_pago UUID NOT NULL,
   monto_total NUMERIC(10,2) NOT NULL CHECK (monto_total > 0),
   comision_plataforma NUMERIC(10,2) NOT NULL CHECK (comision_plataforma >= 0),
   monto_neto_tecnico NUMERIC(10,2) NOT NULL CHECK (monto_neto_tecnico >= 0),
   impuesto_total NUMERIC(10,2) NOT NULL CHECK (impuesto_total >= 0),
   estado estado_pago NOT NULL DEFAULT 'PENDIENTE',
   transaction_id VARCHAR(255),
   fecha_pago TIMESTAMPTZ,
   creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE,
   FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago),
   CONSTRAINT chk_suma_montos CHECK (monto_total = (comision_plataforma + monto_neto_tecnico + impuesto_total))
);

CREATE INDEX idx_pagos_estado ON pagos(estado);

CREATE TABLE facturas (
   id_factura UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   id_pago UUID NOT NULL,
   codigo_comprobante VARCHAR(100) UNIQUE NOT NULL,
   tipo tipo_comprobante NOT NULL DEFAULT 'BOLETA',
   estado_fiscal estado_fiscal_comprobante NOT NULL DEFAULT 'PENDIENTE',
   url_pdf TEXT,
   id_factura_referencia UUID, -- Clave autoreferenciada legal para anulación mediante Notas de Crédito
   fecha_emision TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE CASCADE,
   FOREIGN KEY (id_factura_referencia) REFERENCES facturas(id_factura) ON DELETE SET NULL
);

CREATE INDEX idx_facturas_codigo ON facturas(codigo_comprobante);
