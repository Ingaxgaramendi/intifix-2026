-- ============================================================================
-- Índices de búsqueda adicionales (idempotente: sí se ejecuta sobre la BD
-- cloud ya provisionada, donde V1 quedó como baseline).
-- ============================================================================

-- pg_trgm puede no estar permitida para el usuario en PostgreSQL gestionado:
-- no debe romper la migración.
DO $$
BEGIN
    CREATE EXTENSION IF NOT EXISTS pg_trgm;
EXCEPTION
    WHEN insufficient_privilege THEN
        RAISE NOTICE 'Sin privilegios para crear pg_trgm; se omiten los indices trigram.';
END
$$;

-- Soporta LOWER(nombres_completos) LIKE '%texto%' sin seq scan.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pg_trgm') THEN
        CREATE INDEX IF NOT EXISTS ix_perfiles_cliente_nombres_trgm
            ON perfiles_cliente USING gin (lower(nombres_completos) gin_trgm_ops);
        CREATE INDEX IF NOT EXISTS ix_perfiles_tecnico_nombres_trgm
            ON perfiles_tecnico USING gin (lower(nombres_completos) gin_trgm_ops);
    END IF;
END
$$;
