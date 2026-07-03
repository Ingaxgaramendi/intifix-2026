-- ============================================================================
-- Catálogo inicial de métodos de pago de la pasarela.
-- Idempotente: ON CONFLICT (nombre) DO NOTHING, así puede re-aplicarse sin
-- duplicar ni fallar si alguno ya existe.
-- Los nombres coinciden con el mapeo del frontend (Tarjeta / Yape / Plin).
-- ============================================================================

INSERT INTO metodo_pago (nombre) VALUES
    ('Tarjeta de crédito/débito'),
    ('Yape'),
    ('Plin')
ON CONFLICT (nombre) DO NOTHING;
