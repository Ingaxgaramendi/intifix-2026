-- ============================================================================
-- INTIFIX v3: MongoDB collections initialization (pseudo-SQL documentation)
-- ============================================================================
-- This migration is informational. Execute MongoDB commands directly.
--
-- MongoDB collections required:
-- =======================================================================

-- db.conversaciones.createCollection({
--   validator: {
--     $jsonSchema: {
--       bsonType: 'object',
--       required: ['servicioId', 'participantes', 'createdAt'],
--       properties: {
--         _id: { bsonType: 'objectId' },
--         servicioId: { bsonType: 'string' },
--         participantes: { 
--           bsonType: 'array',
--           items: {
--             bsonType: 'object',
--             properties: {
--               usuarioId: { bsonType: 'string' },
--               nombre: { bsonType: 'string' },
--               rol: { bsonType: 'string' },
--               joinedAt: { bsonType: 'date' },
--               leftAt: { bsonType: ['date', 'null'] }
--             }
--           }
--         },
--         estado: { enum: ['ACTIVA', 'ARCHIVADA', 'CERRADA'] },
--         lastMessageAt: { bsonType: ['date', 'null'] },
--         lastMessagePreview: { bsonType: 'string' },
--         unreadCount: { bsonType: 'int' },
--         createdAt: { bsonType: 'date' },
--         updatedAt: { bsonType: 'date' }
--       }
--     }
--   }
-- })
-- 
-- db.conversaciones.createIndex({ 'servicioId': 1 })
-- db.conversaciones.createIndex({ 'participantes.usuarioId': 1 })
-- db.conversaciones.createIndex({ 'createdAt': -1 })
-- db.conversaciones.createIndex({ 'lastMessageAt': -1 })

-- db.mensajes.createCollection({
--   validator: {
--     $jsonSchema: {
--       bsonType: 'object',
--       required: ['conversacionId', 'remitenteId', 'contenido', 'tipo', 'createdAt'],
--       properties: {
--         _id: { bsonType: 'objectId' },
--         conversacionId: { bsonType: 'string' },
--         remitenteId: { bsonType: 'string' },
--         contenido: { bsonType: 'string' },
--         tipo: { enum: ['TEXTO', 'IMAGEN', 'VIDEO', 'UBICACION', 'ARCHIVO', 'NOTIFICACION'] },
--         estado: { enum: ['ENVIADO', 'ENTREGADO', 'LEIDO', 'FALLIDO'] },
--         replyToMessageId: { bsonType: ['objectId', 'null'] },
--         mediaUrl: { bsonType: ['string', 'null'] },
--         mediaMetadata: {
--           bsonType: ['object', 'null'],
--           properties: {
--             mimeType: { bsonType: 'string' },
--             size: { bsonType: 'int' },
--             width: { bsonType: ['int', 'null'] },
--             height: { bsonType: ['int', 'null'] }
--           }
--         },
--         latitud: { bsonType: ['double', 'null'] },
--         longitud: { bsonType: ['double', 'null'] },
--         ubicacionNombre: { bsonType: ['string', 'null'] },
--         editedAt: { bsonType: ['date', 'null'] },
--         deletedAt: { bsonType: ['date', 'null'] },
--         attachmentCount: { bsonType: 'int', default: 0 },
--         emojiReactions: {
--           bsonType: 'object',
--           additionalProperties: { bsonType: 'array', items: { bsonType: 'string' } }
--         },
--         createdAt: { bsonType: 'date' },
--         updatedAt: { bsonType: 'date' }
--       }
--     }
--   }
-- })
--
-- db.mensajes.createIndex({ 'conversacionId': 1, 'createdAt': -1 })
-- db.mensajes.createIndex({ 'remitenteId': 1 })
-- db.mensajes.createIndex({ 'createdAt': -1 })
-- db.mensajes.createIndex({ 'estado': 1 })

-- db.mensajes_leidos.createCollection({})
-- db.mensajes_leidos.createIndex({ 'usuarioId': 1, 'conversacionId': 1 }, { unique: true })
-- db.mensajes_leidos.createIndex({ 'timestampLeido': -1 })

-- db.notificaciones.createCollection({
--   validator: {
--     $jsonSchema: {
--       bsonType: 'object',
--       required: ['usuarioId', 'tipo', 'createdAt'],
--       properties: {
--         _id: { bsonType: 'objectId' },
--         usuarioId: { bsonType: 'string' },
--         tipo: { bsonType: 'string' },
--         titulo: { bsonType: 'string' },
--         mensaje: { bsonType: 'string' },
--         leida: { bsonType: 'bool' },
--         referenceId: { bsonType: ['string', 'null'] },
--         referenceType: { enum: ['SERVICIO', 'PAGO', 'COTIZACION', 'MENSAJE', null] },
--         actionUrl: { bsonType: ['string', 'null'] },
--         iconUrl: { bsonType: ['string', 'null'] },
--         prioridad: { enum: ['BAJA', 'NORMAL', 'ALTA', 'CRITICA'] },
--         emailPayload: {
--           bsonType: ['object', 'null'],
--           properties: {
--             destinatario: { bsonType: 'string' },
--             asunto: { bsonType: 'string' },
--             template: { bsonType: 'string' },
--             variables: { bsonType: 'object' },
--             enviada: { bsonType: 'bool' },
--             intentos: { bsonType: 'int' }
--           }
--         },
--         createdAt: { bsonType: 'date' },
--         leida_at: { bsonType: ['date', 'null'] },
--         expiresAt: { bsonType: ['date', 'null'] }
--       }
--     }
--   }
-- })
--
-- db.notificaciones.createIndex({ 'usuarioId': 1, 'createdAt': -1 })
-- db.notificaciones.createIndex({ 'tipo': 1 })
-- db.notificaciones.createIndex({ 'leida': 1 })
-- db.notificaciones.createIndex({ 'expiresAt': 1 }, { expireAfterSeconds: 0 })

-- db.logs_api.createCollection({})
-- db.logs_api.createIndex({ 'path': 1 })
-- db.logs_api.createIndex({ 'createdAt': -1 })
-- db.logs_api.createIndex({ 'statusCode': 1 })

-- db.logs_errores.createCollection({})
-- db.logs_errores.createIndex({ 'clase': 1 })
-- db.logs_errores.createIndex({ 'createdAt': -1 })

-- db.logs_seguridad.createCollection({})
-- db.logs_seguridad.createIndex({ 'usuarioId': 1, 'createdAt': -1 })
-- db.logs_seguridad.createIndex({ 'tipoEvento': 1 })

-- db.recomendaciones_ia.createCollection({})
-- db.recomendaciones_ia.createIndex({ 'servicioId': 1 })
-- db.recomendaciones_ia.createIndex({ 'diagnosticoId': 1 })
-- db.recomendaciones_ia.createIndex({ 'createdAt': -1 })

-- db.ubicaciones_live_tecnicos.createCollection({})
-- db.ubicaciones_live_tecnicos.createIndex({ 'usuarioId': 1 }, { unique: true })
-- db.ubicaciones_live_tecnicos.createIndex({ 'location': '2dsphere' })
-- db.ubicaciones_live_tecnicos.createIndex({ 'ultimaActualizacion': 1 })

-- db.activity_feed.createCollection({})
-- db.activity_feed.createIndex({ 'usuarioId': 1, 'createdAt': -1 })
-- db.activity_feed.createIndex({ 'actividadTipo': 1 })

-- db.eventos_sistema.createCollection({})
-- db.eventos_sistema.createIndex({ 'evento': 1, 'createdAt': -1 })

-- db.tracking_servicio.createCollection({})
-- db.tracking_servicio.createIndex({ 'servicioId': 1, 'timestamp': -1 })

-- ============================================================================
-- Sample data initialization for PostgreSQL (optional)
-- ============================================================================

-- Insert admin user (password: admin123 - CHANGE IN PRODUCTION)
INSERT INTO usuarios (
    correo, dni, password_hash, telefono, rol, estado, verificado,
    intentos_fallidos, dos_factor_enabled
) VALUES (
    'admin@intifix.local',
    '1234567890',
    -- Hash of 'admin123' using bcrypt (example)
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWDeeoIiZyDu9Om',
    '+56912345678',
    'ADMIN',
    'ACTIVO',
    true,
    0,
    false
) ON CONFLICT (correo) DO NOTHING;

-- Insert test client user
INSERT INTO usuarios (
    correo, dni, password_hash, telefono, rol, estado, verificado
) VALUES (
    'cliente@intifix.local',
    '9876543210',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWDeeoIiZyDu9Om',
    '+56912345679',
    'CLIENTE',
    'ACTIVO',
    true
) ON CONFLICT (correo) DO NOTHING;

-- Insert test technician user
INSERT INTO usuarios (
    correo, dni, password_hash, telefono, rol, estado, verificado
) VALUES (
    'tecnico@intifix.local',
    '1112223334',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWDeeoIiZyDu9Om',
    '+56912345680',
    'TECNICO',
    'ACTIVO',
    true
) ON CONFLICT (correo) DO NOTHING;

-- ============================================================================
-- Redis key patterns (Reference - no direct migration needed)
-- ============================================================================
-- refresh_token:{userId} = {token}:{expiresAt}
-- login_attempts:{email}:{ip} = {count}:{lockedUntil}
-- rate_limit:{userId}:{endpoint} = {requestCount}:{resetAt}
-- session:{sessionId} = {userId}:{expiresAt}
-- cache:servicios:{id} = {json}
-- cache:tecnicos:{especialidad} = {json}

-- ============================================================================
-- NOTES FOR DEPLOYMENT
-- ============================================================================
-- 1. Run V1 (init.sql) against PostgreSQL
-- 2. Run V2 (advanced_indexes_and_views.sql) against PostgreSQL
-- 3. Initialize MongoDB collections using provided commands
-- 4. Verify all indexes are created
-- 5. Run application with Spring Boot to validate Hibernate mappings
-- 6. Update application.properties with actual DB URLs
-- 7. Enable database replication/clustering for production
