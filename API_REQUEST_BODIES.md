# API_REQUEST_BODIES — Bodies exactos de Intifix (POST/PUT/PATCH)

> Complemento de `FRONTEND_PROMPT.md`. Sacado **directo de los DTOs Java** del backend.
> Úsalo como fuente de verdad para los request: nombres de campo, obligatoriedad, límites y enums.
> Recuerda: todo va con `Authorization: Bearer <accessToken>` (salvo login/register/refresh) y la
> respuesta siempre viene envuelta en `{ success, message, data, timestamp }`.

Convención: `*` = obligatorio. Lo demás es opcional (puedes omitirlo).

---

## ⚠️ AVISOS QUE TE AHORRAN ERRORES 400/403

1. **`responder cotización` NO es booleano.** Usa `{ "estado": "ACEPTADA" | "RECHAZADA", "motivo"? }`.
2. **`asignar técnico`**: el campo es **`idUsuarioTecnico`** (no `idTecnico`) y **`idCotizacion` es obligatorio**.
3. **Ubicación (`idUbicacion`)**: `CrearServicioRequest` y la ubicación del técnico exigen un
   **`idUbicacion`**. ✅ Ya existe endpoint para crearla: **`POST /api/v1/ubicaciones`** (ver sección
   UBICACIONES abajo). Flujo: el front toma lat/lng del mapa → `POST /api/v1/ubicaciones` → usa el
   `idUbicacion` devuelto al crear el servicio o asignar la ubicación del técnico.
4. **Ubicación del técnico se asigna por QUERY PARAM, no body:**
   `PUT /api/v1/technicians/{idUsuario}/location?idUbicacion=<uuid>` (idem `PATCH`).
5. Varios endpoints son **solo ADMIN** (van en el panel Django, no en este front): crear método de pago,
   confirmar/reembolsar pago, crear factura, aprobar/rechazar técnico, filtrar servicios por estado.
   Marcados abajo con `[ADMIN]`.

---

## AUTH (`/api/v1/auth`) — público

```jsonc
// POST /register
{ "correo": "a@b.com"*, "clave": "8-100 chars"*, "telefono": "10-20 dígitos"*, "roles": ["CLIENTE"]* }  // o ["TECNICO"]
// POST /login
{ "correo": "a@b.com"*, "clave": "..."* }
// POST /refresh
{ "refreshToken": "..."* }
// POST /logout
{ "refreshToken": "..."* }
```

## UBICACIONES (`/api/v1/ubicaciones`) — cualquier usuario autenticado

```jsonc
// POST /            (registra un punto del mapa; devuelve data.idUbicacion)
{
  "departamento": "Lima"*,        // ≤100
  "provincia": "Lima"*,           // ≤100
  "distrito": "Miraflores"*,      // ≤100
  "direccionTexto": "Av. Pardo 123"*,  // ≤255
  "referencia": "Frente al parque", // ≤500
  "latitud": -12.1211000*,        // -90..90, hasta 7 decimales
  "longitud": -77.0290000*        // -180..180, hasta 7 decimales
}
// Respuesta: data = { idUbicacion, departamento, provincia, distrito, direccionTexto, referencia, latitud, longitud }
// GET /{idUbicacion}   -> mismos campos
```

> Flujo recomendado: mapa Leaflet → el usuario marca el punto → `POST /api/v1/ubicaciones` →
> con el `idUbicacion` que devuelve, llamas a `POST /api/v1/services` o asignas la ubicación del técnico.

## CLIENTES (`/api/v1/clientes`)

```jsonc
// POST /            (crear perfil cliente; idUsuario = id del usuario registrado)
{ "idUsuario": "uuid"*, "nombresCompletos": "2-255"*, "dniRuc": "8 u 11 dígitos", "fotoPerfilUrl": "https://..." }
// PATCH /{idUsuario}   (todos opcionales; manda solo lo que cambia)
{ "nombresCompletos": "2-255", "dniRuc": "8 u 11 dígitos", "fotoPerfilUrl": "https://..." }
```

## SERVICIOS (`/api/v1/services`)

```jsonc
// POST /            (el idCliente lo deduce el backend del token; NO lo mandes)
{
  "idUbicacion": "uuid"*,            // ⚠️ ver aviso 3
  "idEspecialidad": "uuid"*,         // especialidad del servicio (compu, gasfitería, etc.)
  "titulo": "5-255"*,
  "descripcion": "10-2000"*,
  "modalidad": "EN_CASA_CLIENTE"*,   // EN_CASA_CLIENTE | EN_TALLER_TECNICO
  "prioridad": "MEDIA"*,             // BAJA | MEDIA | ALTA | URGENTE
  "presupuestoMaximo": 250.00,       // 0.01 – 999999.99
  "fechaProgramada": "2026-06-20T10:00:00Z"*   // ISO, futura
}
// PUT /{idServicio}   (todos opcionales)
{ "idEspecialidad": "uuid", "titulo": "5-255", "descripcion": "10-2000", "modalidad": "...", "prioridad": "...", "presupuestoMaximo": 0, "fechaProgramada": "ISO futura" }
// PATCH /{idServicio}/estado
{ "estado": "EN_PROCESO"*, "comentario": "≤500" }
// estado: PENDIENTE | COTIZANDO | ASIGNADO | EN_PROCESO | FINALIZADO | CANCELADO
```

## COTIZACIONES (`/api/v1/services/cotizaciones`)

```jsonc
// POST /            [TECNICO] envía su propuesta
{ "idServicio": "uuid"*, "precio": 150.00*, "tiempoEstimado": "≤100 chars (ej '2 horas')"*, "comentario": "≤1000" }
// PATCH /{idCotizacion}/responder   [CLIENTE] acepta/rechaza
{ "estado": "ACEPTADA"*, "motivo": "≤500" }
// estado: PENDIENTE | ACEPTADA | RECHAZADA | EXPIRADA   (para responder usa ACEPTADA o RECHAZADA)
```

## ASIGNACIONES (`/api/v1/services/asignaciones`)

```jsonc
// POST /{idServicio}/asignar   [CLIENTE] (tras aceptar una cotización)
{
  "idUsuarioTecnico": "uuid"*,
  "idCotizacion": "uuid"*,
  "fechaInicioEstimada": "2026-06-20T10:00:00Z",   // presente/futura
  "fechaFinEstimada": "2026-06-20T14:00:00Z",      // futura
  "notasTecnico": "≤500",
  "coordenadaEncuentroLat": -12.04,   // -90..90
  "coordenadaEncuentroLng": -77.04,   // -180..180
  "direccionEncuentro": "≤500"
}
// PUT /{idAsignacion}   [CLIENTE]   -> MISMO body que asignar (AsignarTecnicoRequest)
// PATCH /{idAsignacion}/iniciar     [TECNICO]  -> SIN body
// PATCH /{idAsignacion}/finalizar   [TECNICO]  -> SIN body
```

## CALIFICACIONES (`/api/v1/services/calificaciones`)

```jsonc
// POST /   [CLIENTE]   (puntuaciones de 1 a 5)
{
  "idServicio": "uuid"*,
  "puntuacion": 5*,                  // 1-5
  "comentario": "≤1000",
  "puntualidad": 5,                  // 1-5
  "profesionalismo": 5,              // 1-5
  "calidadTrabajo": 5,               // 1-5
  "comunicacion": 5,                 // 1-5
  "recomendaria": true,
  "aspectosPositivos": ["puntual", "limpio"],
  "aspectosMejorar": ["precio"]
}
```

## EVIDENCIAS (`/api/v1/services/evidencias`)

```jsonc
// POST /   (subes la URL del archivo ya alojado; el backend no recibe el binario)
{
  "idServicio": "uuid"*,
  "urlArchivo": "https://..."*,      // ≤1000
  "nombreArchivo": "foto.jpg"*,      // ≤255
  "tipoArchivo": "IMAGEN"*,          // IMAGEN | VIDEO | PDF
  "tamanoBytes": 123456,             // > 0
  "descripcion": "≤500",
  "subidoPor": "uuid"*               // id del usuario que sube
}
```

## REPORTES (`/api/v1/services/reportes`)

```jsonc
// POST /
{
  "idServicio": "uuid", "idReportado": "uuid",
  "tipoReporte": "≤50"*, "motivo": "10-500"*,
  "descripcionDetallada": "≤2000", "prioridad": "≤20",
  "evidenciasUrl": ["https://..."]
}
```

## PAGOS (`/api/v1/payments`)

```jsonc
// POST /            [CLIENTE]   (montos con 2 decimales)
{
  "idServicio": "uuid"*, "idMetodoPago": "uuid"*,
  "montoTotal": 250.00*,           // > 0
  "comisionPlataforma": 25.00*,    // >= 0
  "montoNetoTecnico": 200.00*,     // >= 0
  "impuestoTotal": 25.00*          // >= 0
}
// POST /procesar    [CLIENTE]
{ "idPago": "uuid"*, "descripcion": "≤500", "metadata": { "clave": "valor" } }
// POST /{idPago}/confirmar   [ADMIN]   { "idPago": "uuid"*, "transactionId": "≤255"* }
// POST /reembolsar           [ADMIN]   { "idPago": "uuid"*, "razon": "10-500"* }
```

### Métodos de pago / Facturas

```jsonc
// POST /api/v1/payments/methods    [ADMIN]   { "nombre": "3-100"* }
// POST /api/v1/payments/invoices
{ "idPago": "uuid"*, "tipo": "BOLETA"*, "codigoComprobante": "≤100", "idFacturaReferencia": "uuid" }
// tipo: BOLETA | FACTURA | NOTA_CREDITO
```

## CHAT (`/api/v1/chat`) — ver también el contrato STOMP en FRONTEND_PROMPT.md

```jsonc
// POST /conversaciones        { "idServicio": "uuid"* }
// POST /mensajes              (también se puede mandar por STOMP /app/chat.send)
{
  "idConversacion": "uuid"*,
  "tipo": "TEXTO"*,            // TEXTO | IMAGEN | VIDEO | AUDIO | PDF  (default TEXTO)
  "contenido": "≤4000",       // obligatorio si tipo=TEXTO
  "adjunto": {                 // obligatorio si tipo es de archivo
    "url": "https://..."*, "nombreArchivo": "≤255"*, "tipoMime": "≤100"*, "tamanoBytes": 123
  },
  "idMensajeRespondido": "uuid"   // opcional (responder en hilo)
}
// PUT /mensajes/{id}          { "contenido": "≤4000"* }
```

## TÉCNICOS (`/api/v1/technicians`)

```jsonc
// POST /            (crear perfil técnico; idUsuario = usuario registrado con rol TECNICO)
{
  "idUsuario": "uuid"*,
  "nombresCompletos": "≤255"*,
  "dniRuc": "8-20 dígitos"*,
  "experienciaAnios": 5*,            // 0-50
  "estadoAprobacion": "PENDIENTE"*,  // PENDIENTE | APROBADO | RECHAZADO
  "disponibilidad": "DISPONIBLE"*,   // DISPONIBLE | OCUPADO
  "tarifaBase": 50.00*,              // 0.01-99999.99
  "dniFrontalUrl": "https://...", "dniTraseroUrl": "https://...",
  "antecedentePenalUrl": "https://...", "certificadoTecnicoUrl": "https://...",
  "idUbicacion": "uuid"*             // ⚠️ ver aviso 3
}
// PUT /{idUsuario}                (ActualizarTecnicoRequest — todos opcionales)
// PATCH /{idUsuario}/documentos   (mismo DTO; manda solo las *Url que cambian)
{ "dniFrontalUrl": "https://...", "dniTraseroUrl": "https://...", "antecedentePenalUrl": "...", "certificadoTecnicoUrl": "..." }
// PATCH /{idUsuario}/disponibilidad
{ "disponibilidad": "OCUPADO"* }    // DISPONIBLE | OCUPADO
// PATCH /{idUsuario}/aprobar   [ADMIN]      PATCH /{idUsuario}/rechazar   [ADMIN]
// PUT|PATCH /{idUsuario}/location?idUbicacion=<uuid>   ← QUERY PARAM, sin body
```

### Especialidades (`/api/v1/technicians/specialties`)

```jsonc
// POST /            { "nombre": "3-150, solo letras"*, "descripcion": "≤1000" }
// PUT /{id}         { "nombre": "3-150", "descripcion": "≤1000" }      (opcionales)
// POST /asignar     { "idUsuarioTecnico": "uuid"*, "idEspecialidad": "uuid"* }
```

### Horarios (`/api/v1/technicians/schedules`)

```jsonc
// POST /
{ "idUsuarioTecnico": "uuid"*, "diaSemana": 1*, "horaInicio": "08:00"*, "horaFin": "17:00"*, "activo": true* }
// diaSemana: 0=domingo … 6=sábado. horaFin DEBE ser posterior a horaInicio.
// PUT /{idHorario}   (opcionales) { "diaSemana": 0-6, "horaInicio": "HH:mm", "horaFin": "HH:mm", "activo": true }
```

### Excepciones de horario (`/api/v1/technicians/schedule-exceptions`)

```jsonc
// POST /
{ "idUsuarioTecnico": "uuid"*, "fechaInicio": "ISO"*, "fechaFin": "ISO"*, "motivo": "10-500"* }
// fechaFin DEBE ser posterior a fechaInicio.
```

## NOTIFICACIONES (`/api/v1/notifications`)

Solo lecturas y cambios por path — **sin body**: `PATCH /{id}/leer`, `PATCH /leer-todas`, `DELETE /{id}`.

---

### Tipos comunes

- **UUID**: string tipo `"3fa85f64-5717-4562-b3fc-2c963f66afa6"`.
- **Fechas**: ISO-8601 (`ZonedDateTime` → `"2026-06-20T10:00:00Z"`; `LocalTime` → `"08:00"`).
- **Montos**: número decimal (BigDecimal), máx 2 decimales.
- Enviar campos opcionales en `null` o ausentes es equivalente; no mandes strings vacíos donde se valida formato (URL, DNI).
