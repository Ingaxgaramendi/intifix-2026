# Flyway

INTIFIX usa el esquema **PostgreSQL Enterprise** ya desplegado en la nube. Las migraciones Flyway están **desactivadas por defecto**.

- Activa solo en entornos locales con esquema vacío: `INTIFIX_FLYWAY_ENABLED=true`
- El DDL de referencia debe mantenerse igual al de tu nube (colecciones Mongo + tablas PostgreSQL).
