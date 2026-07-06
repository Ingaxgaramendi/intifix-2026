-- Ensure presupuesto_maximo is positive when provided.
-- IS NULL branch keeps historical rows (created before this field was required).
ALTER TABLE servicios
    ADD CONSTRAINT chk_presupuesto_positivo
    CHECK (presupuesto_maximo IS NULL OR presupuesto_maximo > 0);
