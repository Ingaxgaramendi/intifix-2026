-- ============================================================================
-- V9: Ubicación base/guardada del cliente.
-- Habilita el orden "técnicos más cercanos" (distancia cliente→técnico) y el
-- perfil público que el técnico ve (solo distrito/zona, nunca dirección exacta).
-- Nullable: los clientes existentes no tienen ubicación hasta que la fijen.
-- Espejo de perfiles_tecnico.id_ubicacion (V1/PerfilTecnico).
-- ============================================================================
ALTER TABLE perfiles_cliente
   ADD COLUMN id_ubicacion UUID REFERENCES ubicaciones(id_ubicacion);

CREATE INDEX idx_clientes_ubicacion ON perfiles_cliente(id_ubicacion) WHERE id_ubicacion IS NOT NULL;
