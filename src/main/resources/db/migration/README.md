# Flyway

INTIFIX usa el esquema **PostgreSQL Enterprise** desplegado en la nube.

- `V1__esquema_base.sql` — espejo exacto del esquema productivo. En la BD cloud
  se marca como **baseline** sin ejecutarse (`baseline-on-migrate: true`,
  `baseline-version: 1` en application.yml). En BDs locales vacías crea todo.
- `V2__indices_busqueda.sql` — índices trigram para búsqueda por nombre
  (idempotente; sí corre sobre la BD cloud).

Reglas:

1. Todo cambio de esquema nuevo entra como `V{n+1}__descripcion.sql`. Nunca
   editar una migración ya aplicada.
2. Si la BD cloud tiene una tabla `flyway_schema_history` antigua (de las
   migraciones previas a junio 2026), eliminarla una sola vez
   (`DROP TABLE flyway_schema_history;`) para que el baseline se regenere.
