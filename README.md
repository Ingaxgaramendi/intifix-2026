# INTIFIX Backend — Enterprise Technical Services Marketplace

**Production-ready** modular monolith (Java 21 + Spring Boot 3.3) powering a global technical services platform similar to TaskRabbit or Thumbtack.

## 🏗️ Tech Stack

| Component       | Technology        | Purpose                                                |
| --------------- | ----------------- | ------------------------------------------------------ |
| Runtime         | Java 21 LTS       | Modern, stable VM with records & pattern matching      |
| Framework       | Spring Boot 3.3   | Reactive-ready, cloud-native defaults                  |
| **RDBMS**       | PostgreSQL 14+    | Transactional consistency (users, payments, contracts) |
| **NoSQL**       | MongoDB 5.0+      | High-throughput chat, notifications, logs, AI history  |
| **Cache/Queue** | Redis 7+          | JWT refresh, login attempt throttling, pub/sub         |
| **Migrations**  | Flyway            | Version-controlled schema (V1–V3 DDL + views)          |
| **Real-time**   | WebSocket (STOMP) | Bi-directional chat, live tech location updates        |

---

## 📦 Modular Architecture

```
src/main/java/com/intifix/
├── auth/                    # JWT (access + refresh), login lockout, 2FA hooks
├── users/                   # Legacy package; models in modules/users
├── modules/
│   ├── users/               # Usuario + PerfilUsuario entities & profiles
│   ├── technicians/         # PerfilTecnico, ReputacionTecnico, matching engine
│   ├── services/            # Servicio, HistorialServicio, assignments
│   ├── quotes/              # Cotizacion lifecycle + acceptance flow
│   ├── payments/            # Pago + Factura (with gateway payloads, refunds)
│   ├── admin/               # Reporte moderation, user suspension
│   ├── ai/                  # DiagnosticoIa + SugerenciaEspecialidad (mock → external)
│   ├── geo/                 # Haversine distance + Mongo 2dsphere indexes
│   ├── chat/                # ConversacionDocument + MensajeDocument (Mongo/WebSocket)
│   ├── notifications/       # NotificacionDocument (in-app + email-ready payloads)
│   └── logging/             # ApiLogDocument, error & security logs
└── shared/
    ├── security/            # RBAC filters, JWT validation, rate limiting
    ├── converter/           # JsonNodeAttributeConverter for JSONB ↔ Postgres/Java
    ├── entity/              # AuditedEntity (soft delete + audit timestamps)
    ├── config/              # Spring Data, MongoDB, Redis, Flyway setup
    └── events/              # Domain event listeners (audit trail, notifications)
```

**Design principle**: Modular monolith ready for **microservices extraction**. Each module is self-contained: `controller` → `service` → `repository`.

---

## 🚀 Quick Start

### Prerequisites

- **Docker & Docker Compose**
- **Java 21+** (or use Maven wrapper)
- **Maven 3.8+**

### Run Stack

```bash
# Start all databases in background
docker compose up -d

# Run application on port 8080
./mvnw spring-boot:run
```

### Environment Variables

All defaults are in [`application.yml`](src/main/resources/application.yml). Override in production:

| Variable                            | Default                                    | Notes                              |
| ----------------------------------- | ------------------------------------------ | ---------------------------------- |
| `INTIFIX_DB_URL`                    | `jdbc:postgresql://localhost:5432/intifix` | PostgreSQL JDBC URL                |
| `INTIFIX_DB_USER`                   | `intifix_app`                              | DB role (created via Flyway V1)    |
| `INTIFIX_DB_PASSWORD`               | `dev_password`                             | **Change in production**           |
| `INTIFIX_MONGO_URI`                 | `mongodb://localhost:27017/intifix`        | MongoDB connection                 |
| `INTIFIX_REDIS_HOST`                | `localhost`                                | Redis hostname                     |
| `INTIFIX_REDIS_PORT`                | `6379`                                     | Redis port                         |
| `INTIFIX_JWT_HMAC_SECRET`           | `dev_secret_min_32_chars_PROD_CHANGE`      | **Min 32 chars, rotate regularly** |
| `INTIFIX_JWT_ACCESS_EXPIRE_MINUTES` | `15`                                       | Access token lifetime              |
| `INTIFIX_JWT_REFRESH_EXPIRE_DAYS`   | `7`                                        | Refresh token lifetime             |

### API Docs

Once running:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🗄️ Database Schema

### PostgreSQL (Flyway V1–V3)

Fully normalized schema with 100% field mappings:

#### **Core User Entities**

| Table              | Fields                                                                                                                                                                                                                                                                                                                                                       | Notes                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------- |
| `usuarios`         | `id_usuario` (PK), `correo`, `dni`, `password_hash`, `telefono`, `rol`, `estado`, `verificado`, `intentos_fallidos`, `ultimo_login`, `fecha_ultimo_cambio_pwd`, `dos_factor_enabled`, `dos_factor_secret`, `ip_registro`, `user_agent_registro`, `metadata` (JSONB), `fecha_registro`, `created_at`, `updated_at`, `deleted_at`                              | Soft delete via `deleted_at`; 2FA fields for future; `metadata` for extensibility |
| `perfiles_usuario` | `id` (PK), `usuario_id` (FK), `nombre`, `apellido`, `telefono_contacto`, `avatar_url`, `biografia`, `idioma`, `timezone`, `coordenada_lat/lng`, `ubicacion_nombre`, `created_at`, `updated_at`                                                                                                                                                               | User profile details; audit timestamps; optional location                         |
| `perfiles_tecnico` | `id` (PK), `usuario_id` (FK, unique), `especialidad`, `anos_experiencia`, `bio`, `certificaciones` (JSONB array), `calificacion_promedio`, `total_servicios`, `total_resenas`, `verificado`, `activo`, `disponibilidad_estado`, `radio_km`, `telefonos_contacto` (text[]), `horario_inicio/fin` (TIME), `dias_laborales` (int[]), `created_at`, `updated_at` | Extensible cert storage via JSONB; array fields for contact & schedules           |

#### **Service & Lifecycle**

| Table                   | Fields                                                                                                                                                                                                                                                                                                                                                                                                                   | Notes                                                            |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------- |
| `servicios`             | `id_servicio` (PK), `id_cliente` (FK), `titulo`, `descripcion`, `categoria_principal`, `especialidad_requerida`, `duracion_estimada_min`, `presupuesto_estimado` (12,2), `coordenada_lat/lng`, `ubicacion_nombre`, `imagenes` (text[]), `fotos_count`, `estado`, `prioridad`, `urgencia_nivel`, `fecha_solicitud_para`, `notas_privadas`, `metadata` (JSONB), `fecha_creacion`, `created_at`, `updated_at`, `deleted_at` | Images array for photos; metadata for extensibility; soft delete |
| `historial_servicio`    | `id_historial` (PK), `id_servicio` (FK), `estado`, `comentario`, `usuario_cambio`, `fecha_cambio`                                                                                                                                                                                                                                                                                                                        | Auto-populated via trigger on `servicios.estado` change          |
| `cotizaciones`          | `id_cotizacion` (PK), `id_servicio` (FK), `id_usuario_tecnico` (FK), `precio` (12,2), `mensaje`, `tiempo_respuesta_min`, `validez_horas` (default 24), `aceptada_en`, `rechazada_en`, `razon_rechazo`, `estado`, `metadata` (JSONB), timestamps                                                                                                                                                                          | Timestamped acceptance/rejection; extensible via JSONB           |
| `asignaciones_servicio` | `id_asignacion` (PK), `id_servicio` (FK, unique), `id_usuario_tecnico` (FK), `id_cotizacion` (FK, unique), `fecha_inicio/fin_estimada/completacion`, `estado`, `notas_tecnico`, `created_at`, `updated_at`                                                                                                                                                                                                               | One assignment per service (enforced via unique constraint)      |

#### **Ratings & Reputation**

| Table                | Fields                                                                                                                                                                                                                                                                   | Notes                                                                    |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------ |
| `calificaciones`     | `id_calificacion` (PK), `id_servicio` (FK, unique), `id_cliente` (FK), `id_usuario_tecnico` (FK), `puntuacion` (1–5), `comentario`, `aspectos_positivos/negativos` (text[]), `puntualidad_rating`, `profesionalismo_rating`, `responde_preguntas`, `fecha`, `created_at` | Immutable once created; arrays for flexible feedback; unique per service |
| `reputacion_tecnico` | `id_usuario_tecnico` (PK), `promedio` (3,2), `total_resenas`, `total_servicios`, `cancelados_cliente/tecnico`, `no_presentes`, `actualizado_en`                                                                                                                          | Aggregate view; updated via trigger on new `calificaciones`              |

#### **Payments & Invoicing**

| Table      | Fields                                                                                                                                                                                                                                                                                                                                                                                                                                             | Notes                                                                               |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| `pagos`    | `id_pago` (PK), `id_servicio` (FK), `cliente_id` (FK), `payee_id` (FK), `id_metodo_pago`, `monto` (12,2), `estado`, `transaction_id`, `referencia`, `gateway_respuesta` (JSONB), `metodo_pago_tipo`, `comision_plataforma` (12,2), `neto_pagado` (12,2), `motivo_fallo`, `reintentos_fallidos`, `fecha_pago`, `fecha_vencimiento`, `reembolso_fecha`, `reembolso_monto`, `reembolso_razon`, `refund_reason_code`, `metadata` (JSONB), timestamps   | Full payment lifecycle; refund tracking; extensible gateway response                |
| `facturas` | `id_factura` (PK), `id_pago` (FK, unique), `cliente_id` (FK), `proveedor_id` (FK), `codigo_factura` (unique), `numero` (unique), `estado_factura`, `subtotal/impuesto/total` (12,2), `descripcion_items`, `condiciones_pago`, `fecha_pago_esperada`, `url_pdf`, `html_factura`, `xml_factura`, `url_electronica`, `numero_electronica`, `fecha_emision`, `fecha_vencimiento`, `fecha_anulacion`, `razon_anulacion`, `metadata` (JSONB), timestamps | Full invoice data (PDF, HTML, XML URLs); electronic invoice support; state tracking |

#### **AI & Diagnostics**

| Table                      | Fields                                                                                                                                                                                  | Notes                                                           |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| `diagnosticos_ia`          | `id` (PK), `servicio_id` (FK), `categoria`, `confianza` (5,2 0–100), `resumen`, `detalles_tecnico` (JSONB), `modelo`, `version_modelo`, `latencia_ms`, `metadata` (JSONB), `created_at` | Mock AI now; extensible via JSONB for external ML integration   |
| `sugerencias_especialidad` | `id` (PK), `diagnostico_id` (FK), `especialidad`, `score` (5,2 0–100), `razon`, `created_at`                                                                                            | Per-service recommendations; multiple suggestions per diagnosis |

#### **Moderation**

| Table      | Fields                                                                                                                                                                                                                                                                                      | Notes                                            |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| `reportes` | `id` (PK), `reportante_id` (FK), `reportado_id` (FK, nullable), `servicio_id` (FK, nullable), `tipo`, `motivo`, `descripcion_detallada`, `evidencia_url` (text[]), `estado`, `prioridad`, `resolucion`, `accion_tomada`, `resuelto_por`, `fecha_resolucion`, `metadata` (JSONB), timestamps | Array for evidence; soft moderation via metadata |

#### **Views (Performance)**

- `v_tecnicos_activos` — Join `perfiles_tecnico` + `usuarios` + `reputacion_tecnico` + `perfiles_usuario`; filters active, verified technicians with rating
- `v_servicios_por_cotizar` — Services in PENDIENTE state with quote count; for matching engine
- `v_resumen_pago_servicio` — Payments + facturas LEFT JOIN; unified billing view

#### **Indexes & Optimization**

- **Partial indexes** on `deleted_at IS NULL`, `estado IN (...)` for soft deletes & state filters
- **Composite indexes** on frequent queries: `(especialidad, activo, verificado)`, `(estado, created_at DESC)`, etc.
- **JSONB GIN indexes** on `metadata` columns for complex queries (optional, add as needed)

---

### MongoDB Collections

Real-time, high-throughput storage (no strict schema enforcement in app; Flyway V3 documents collection creation for reference):

#### **Chat**

| Collection        | Fields                                                                                                                                                                                                                                   | Indexes                                                                | Notes                                                                                      |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| `conversaciones`  | `_id`, `servicioId`, `participantes[{ usuarioId, nombre, rol, joinedAt, leftAt }]`, `estado`, `createdAt`, `updatedAt`, `lastMessageAt`, `lastMessagePreview`, `unreadCount`                                                             | `servicioId`, `participantes.usuarioId`, `createdAt`, `lastMessageAt`  | One per service; stores participant join/leave history                                     |
| `mensajes`        | `_id`, `conversacionId`, `remitenteId`, `contenido`, `tipo`, `estado`, `replyToMessageId`, `mediaUrl`, `mediaMetadata`, `latitud/longitud`, `ubicacionNombre`, `createdAt`, `editedAt`, `deletedAt`, `attachmentCount`, `emojiReactions` | `conversacionId, createdAt DESC`, `remitenteId`, `createdAt`, `estado` | Soft delete via `deletedAt`; emoji reactions stored as `{ "👍": ["user1", "user2"], ... }` |
| `mensajes_leidos` | `_id`, `usuarioId`, `conversacionId`, `mensajeId`, `timestampLeido`                                                                                                                                                                      | `(usuarioId, conversacionId)` unique                                   | Read receipts; allows per-message tracking                                                 |

#### **Notifications**

| Collection       | Fields                                                                                                                                                                             | Indexes                                                      | Notes                                                                     |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------------------- |
| `notificaciones` | `_id`, `usuarioId`, `tipo`, `titulo`, `mensaje`, `leida`, `referenceId`, `referenceType`, `actionUrl`, `iconUrl`, `prioridad`, `emailPayload`, `createdAt`, `leidaAt`, `expiresAt` | `(usuarioId, createdAt)`, `tipo`, `leida`, `expiresAt` (TTL) | Email payload ready for async delivery; TTL auto-delete after `expiresAt` |

#### **Logging & Events**

| Collection           | Fields                                                                                          | Indexes                                    | Notes                                       |
| -------------------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------ | ------------------------------------------- |
| `logs_api`           | `_id`, `method`, `path`, `statusCode`, `durationMs`, `ip`, `userAgent`, `createdAt`             | `path`, `createdAt`, `statusCode`          | Lightweight API audit trail                 |
| `logs_errores`       | `_id`, `clase`, `mensaje`, `stackTrace`, `usuarioId`, `requestId`, `createdAt`                  | `clase`, `createdAt`                       | Application error tracking                  |
| `logs_seguridad`     | `_id`, `usuarioId`, `tipoEvento` (login, 2fa_fail, report, etc.), `detalles`, `ip`, `createdAt` | `(usuarioId, createdAt)`, `tipoEvento`     | Security event audit                        |
| `recomendaciones_ia` | `_id`, `servicioId`, `diagnosticoId`, `payload` (JSONB), `createdAt`                            | `servicioId`, `diagnosticoId`, `createdAt` | Full AI recommendation payload (extensible) |

#### **Geo & Activity**

| Collection                  | Fields                                                                         | Indexes                                   | Notes                                                        |
| --------------------------- | ------------------------------------------------------------------------------ | ----------------------------------------- | ------------------------------------------------------------ |
| `ubicaciones_live_tecnicos` | `_id`, `usuarioId`, `location` (GeoJSON point), `ultimaActualizacion`, TTL     | `(usuarioId)` unique, `location 2dsphere` | Live tech location; auto-expires stale entries               |
| `activity_feed`             | `_id`, `usuarioId`, `actividadTipo`, `referenceId`, `descripcion`, `createdAt` | `(usuarioId, createdAt)`, `actividadTipo` | User activity timeline                                       |
| `eventos_sistema`           | `_id`, `evento`, `detalles` (JSONB), `createdAt`                               | `(evento, createdAt)`                     | System-wide event log (payments completed, dispatches, etc.) |
| `tracking_servicio`         | `_id`, `servicioId`, `evento`, `timestamp`, `metadata`                         | `(servicioId, timestamp)`                 | Per-service event tracking (for analytics, replay)           |

---

### Redis (Key Patterns)

No schema; application manages all keys. Examples:

```
refresh_token:{userId}        → {token}:{expiresAt}
login_attempts:{email}:{ip}   → {count}:{lockedUntil}
rate_limit:{userId}:{endpoint} → {requestCount}:{resetAt}
session:{sessionId}           → {userId}:{expiresAt}
cache:servicios:{id}          → {JSON}
cache:tecnicos:{especialidad} → [{JSON}, ...]
```

---

## 🔐 Security & RBAC

| Role        | Capabilities                                                                         | Constraints                                                   |
| ----------- | ------------------------------------------------------------------------------------ | ------------------------------------------------------------- |
| **CLIENTE** | Create services, browse techs, accept quotes, pay, rate, chat                        | Cannot modify others' services; rated once per service        |
| **TECNICO** | View available services, submit quotes, update location/profile, complete work, chat | Cannot quote own services; location updated ≤ 5 min intervals |
| **ADMIN**   | Moderation dashboard, suspend users, resolve reports, audit logs                     | Cannot perform client/tech actions; all actions logged        |

**Authentication**:

- JWT (HS256 HMAC-SHA256)
- Access token: 15 min (default)
- Refresh token: 7 days (default), stored in Redis
- 2FA hooks ready in `Usuario.dosFactorEnabled` + `dosFactorSecret`

**Rate Limiting**:

- Login attempts: 5 fails per 15 min → 1 hr lockout
- API endpoints: Configurable per-user quotas (Redis-backed)

---

## 🧪 Testing

```bash
# Unit tests (includes auth, geo, AI, matching)
./mvnw test

# Integration tests (Docker required)
./mvnw verify -Dgroups="integration"

# Coverage report
./mvnw jacoco:report
# Open: target/site/jacoco/index.html
```

**Test modules**:

- `auth.*Test` — JWT, login lockout, refresh flow
- `modules/geo.*Test` — Haversine distance, nearby tech queries
- `modules/ai.*Test` — Mock diagnosis, specialty matching
- `modules/technicians.*Test` — Reputation calculations, availability

---

## 🚢 Production Deployment

### Scale & Topology

```
Load Balancer (Azure Application Gateway or ALB)
    ↓
┌─────────────────────────┐
│ INTIFIX API Nodes (3–5) │  ← Stateless; horizontal scale
│ (Java 21 JVM, Spring)   │
└────────┬────────────────┘
         ├─→ PostgreSQL Read Replica (read: list endpoints, notifications)
         ├→  PostgreSQL Primary (write: payments, quotes, changes)
         ├─→ MongoDB Replica Set (read: chat archives, logs)
         ├→  Redis Cluster (session, rate limits, pub/sub)
         └─→ CDN / S3 (invoice PDFs, user avatars, service photos)
```

### Deployment Checklist

- [ ] **Secrets Management**: Rotate `INTIFIX_JWT_HMAC_SECRET` (Azure Key Vault / AWS Secrets Manager)
- [ ] **Database**: Enable SSL/TLS for Postgres & MongoDB; set `ssl=true` in JDBC URL
- [ ] **Redis**: Require password; disable `FLUSHDB` command in ACL
- [ ] **API Gateway**: Enable TLS 1.3, rate limiting, WAF rules (SQL injection, XSS)
- [ ] **Monitoring**: Enable Spring Actuator metrics; configure Prometheus scrape & Grafana dashboards
- [ ] **Logging**: Aggregate logs via ELK Stack or Azure Application Insights
- [ ] **Backups**: Postgres WAL archival; MongoDB point-in-time recovery enabled
- [ ] **CDN**: Cache invoice PDFs, avatars, service photos (immutable URLs with hash)

### Environment Profiles

```yaml
# production.yml (overrides application.yml)
spring:
  jpa:
    hibernate:
      ddl-auto: validate # Never auto-create in prod
  data:
    mongodb:
      auto-index-creation: false # Pre-create indexes
logging:
  level:
    root: INFO
    com.intifix: DEBUG
```

### Horizontal Scale Notes

- **Stateless API**: No session affinity required; JWT contains all auth context
- **Database connections**: HikariCP pool (10–20 per node based on load)
- **MongoDB sharding**: Enable if chat volume > 1M messages/day
- **Cache invalidation**: Use Redis pub/sub for multi-node cache coherence

---

## 🛠️ Development

### IDE Setup

**IntelliJ IDEA**:

- Open `pom.xml` → Load Maven project
- Settings → Build → Compiler → Check "Enable annotation processing"
- Settings → Languages & Frameworks → SQL Dialects → PostgreSQL

**VS Code**:

- Install: Spring Boot Extension Pack, Lombok Annotations Support
- Run via F5 or `./mvnw spring-boot:run`

### Code Generation

```bash
# Generate DDL from entities (validate V1–V3 match)
./mvnw process-classes

# Format code (Google Java Style)
./mvnw spotless:apply

# Run linter (checkstyle + PMD)
./mvnw check
```

### Adding a New Entity

1. Create `src/main/java/com/intifix/modules/{module}/entity/{Entity}.java`
2. Add `@Entity @Table(name = "table_name")` with fields
3. Create migration `src/main/resources/db/migration/VX__description.sql`
4. Create `{Entity}Repository extends JpaRepository<{Entity}, UUID>`
5. Run tests: `./mvnw test`

---

## 📚 References & Docs

- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Spring Data MongoDB**: https://spring.io/projects/spring-data-mongodb
- **Flyway**: https://flywaydb.org/documentation
- **PostgreSQL JSON**: https://www.postgresql.org/docs/current/datatype-json.html
- **MongoDB Aggregation**: https://docs.mongodb.com/manual/reference/operator/aggregation/

---

## 📝 License & Support

**Status**: Production-ready (v0.0.1-SNAPSHOT)  
**Last updated**: May 2026  
**Maintainer**: Intifix Engineering Team

For issues, PRs, or feature requests → [GitHub Issues](#).
