-- ============================================================================
-- V11: Backfill de perfiles_cliente faltantes.
-- Algunos usuarios con rol CLIENTE (registrados antes del auto-aprovisionamiento
-- o cuyo listener no corrió) no tienen fila en perfiles_cliente, lo que rompe
-- "Mi perfil", el perfil público y el dashboard con "Cliente no encontrado".
-- Creamos un perfil mínimo derivando el nombre del correo (el usuario lo edita
-- luego). Idempotente: solo inserta los que faltan.
-- ============================================================================
INSERT INTO perfiles_cliente (id_usuario, nombres_completos)
SELECT u.id_usuario, split_part(u.correo, '@', 1)
FROM usuarios u
JOIN usuario_roles ur ON ur.id_usuario = u.id_usuario AND ur.rol = 'CLIENTE'
WHERE NOT EXISTS (
    SELECT 1 FROM perfiles_cliente pc WHERE pc.id_usuario = u.id_usuario
);
