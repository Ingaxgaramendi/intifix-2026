# PROMPT DEFINITIVO — Frontend Intifix (React Web · Cliente + Técnico)

> Pega TODO lo que está debajo de la línea en tu IA generadora de código (Claude / v0 / Cursor / Bolt).
> Está basado en los endpoints REALES del backend Spring Boot de Intifix.

---

Eres un ingeniero frontend senior. Construye una **aplicación web completa y funcional** para
**Intifix**, un marketplace de servicios técnicos donde **clientes** contratan a **técnicos**.
La app tiene **un solo login** y, según el rol del usuario autenticado (`CLIENTE` o `TECNICO`),
muestra pantallas distintas. (El rol `ADMIN` NO va aquí: tiene su propio panel en Django.)

## Stack obligatorio (úsalo EXACTAMENTE este)
- **React 19** + **TypeScript** + **Vite**
- **Tailwind CSS**
- **shadcn/ui** como librería de componentes (Radix UI por debajo). Usa sus componentes
  (Button, Card, Dialog, Sheet, Tabs, Form, Input, Select, Badge, Avatar, DropdownMenu,
  Skeleton, Sonner/toast, Table, Calendar, etc.) para TODA la UI. Nada de HTML crudo sin estilizar.
- **React Router** para rutas
- **Zustand** para estado global (auth, sesión, UI)
- **Axios** para HTTP + **TanStack Query (@tanstack/react-query)** para fetching/caché/paginación
- **React Hook Form + Zod** para formularios y validación
- **React Leaflet** (+ `leaflet`) para mapas (ubicación de servicios y técnicos)
- **@stomp/stompjs** para tiempo real (chat) — WebSocket nativo, ver contrato STOMP abajo.
  ⚠️ NO uses `socket.io-client` ni `sockjs-client`: el backend es Spring STOMP sobre WS nativo.
- Iconos con `lucide-react`

## Dirección de diseño (MUY IMPORTANTE) — estética **Airbnb**
El look base es **Airbnb: claro, cálido, elegante y premium**. Limpio y confiable, NO un dashboard
administrativo. Requisitos concretos:
- **Tema claro** con mucho aire (fondos blancos / gris muy claro `#F7F7F7`), tipografía legible
  (Inter o similar), jerarquía tipográfica clara con títulos grandes.
- **Color de marca acento estilo Airbnb**: un **coral/rosado** (`~#FF385C`) para botones primarios,
  estados activos y precios. Define los tokens de shadcn/ui acorde (primary = coral).
- Esquinas redondeadas (`rounded-2xl`), **sombras muy suaves**, bordes sutiles, espaciados generosos.
- **Cards tipo Airbnb** para servicios y técnicos: **foto grande arriba**, título, rating con
  estrella (★ 4.9), precio destacado, badges de estado. Listas en **grid responsive** (1→2→3→4 cols).
- Header limpio con buscador prominente tipo Airbnb (barra de búsqueda redondeada con sombra).
- **Mobile-first** y totalmente responsive; sensación de app real.
- **Mapas con React Leaflet** integrados con el mismo estilo cálido (markers redondos con el coral,
  popups/cards limpias). En "buscar técnicos" usa mapa + lista de cards (no full-screen oscuro).
- Microinteracciones suaves (hover con leve elevación de la card, transiciones, skeletons de shadcn).
- Estados vacíos amables e ilustrados, mensajes con buen copy, toasts discretos.
- (Opcional) modo oscuro con el theming de shadcn, pero el modo claro es el principal.

## Backend al que se conecta
- Base URL: `http://localhost:8080` (configúrala en `.env` como `VITE_API_BASE_URL`)
- **Todas** las respuestas vienen envueltas así:
  ```json
  { "success": true, "message": "texto", "data": <LO_QUE_IMPORTA>, "timestamp": "..." }
  ```
  Crea un wrapper de axios que **siempre devuelva `response.data.data`** y, en error,
  muestre `response.data.message` en un toast.
- **Listados paginados**: los endpoints de listas devuelven un `Page` de Spring, o sea
  `data = { content: [...], totalElements, totalPages, number, size }`.
  Se piden con query params `?page=0&size=20&sort=campo,desc`. Implementa paginación real.

## Autenticación (JWT)
- **Login** — `POST /api/v1/auth/login` body `{ "correo": string, "clave": string }`
  → `data = { accessToken, refreshToken, tokenType:"Bearer", expiresIn:900, correo }`
- **Registro** — `POST /api/v1/auth/register` body
  `{ "correo": string, "clave": string(8-100), "telefono": string(10-20 dígitos), "roles": ["CLIENTE"] | ["TECNICO"] }`
  (en el registro el usuario elige si entra como Cliente o como Técnico)
- **Refresh** — `POST /api/v1/auth/refresh` body `{ "refreshToken": string }` → nuevo `data` igual al login
- **Logout** — `POST /api/v1/auth/logout` body `{ "refreshToken": string }`
- **Usuario actual** — `GET /api/v1/auth/current-user` → trae `roles` para decidir qué UI mostrar
- En cada request protegido manda header: `Authorization: Bearer <accessToken>`
- El access token dura **15 min**. Implementa un **interceptor de axios**: ante un `401`,
  intenta `refresh` UNA vez y reintenta la request; si el refresh falla, cierra sesión y redirige a login.
- Guarda tokens en `localStorage`. Rutas protegidas por rol (`<ProtectedRoute roles={['CLIENTE']}>`).

## Roles
`CLIENTE`, `TECNICO`. Tras el login, lee los roles y enruta al dashboard correspondiente.

---

# PANTALLAS A CONSTRUIR

## Comunes (sin login)
1. **Landing** corta con CTA a Login/Registro.
2. **Login** (correo + clave).
3. **Registro** con selector de rol (Cliente / Técnico), correo, clave, teléfono.

## Comunes (con login, ambos roles)
4. **Layout** con barra de navegación lateral/superior que cambia según el rol + botón logout + campana de notificaciones con contador.
5. **Notificaciones**
   - Listar: `GET /api/v1/notifications` (paginado)
   - No leídas: `GET /api/v1/notifications/no-leidas`
   - Contador (para el badge): `GET /api/v1/notifications/contador`
   - Marcar leída: `PATCH /api/v1/notifications/{id}/leer`
   - Marcar todas: `PATCH /api/v1/notifications/leer-todas`
   - Eliminar: `DELETE /api/v1/notifications/{id}`
6. **Chat** (real-time)
   - Conversaciones: `GET /api/v1/chat/conversaciones` (paginado), crear `POST /api/v1/chat/conversaciones`,
     ver `GET /api/v1/chat/conversaciones/{id}`, archivar `PATCH .../archivar`, bloquear `PATCH .../bloquear`, borrar `DELETE .../{id}`
   - Mensajes: listar `GET /api/v1/chat/mensajes/conversacion/{idConversacion}` (paginado),
     enviar `POST /api/v1/chat/mensajes`, editar `PUT /api/v1/chat/mensajes/{id}`, borrar `DELETE .../{id}`,
     marcar leídos `POST /api/v1/chat/mensajes/conversacion/{idConversacion}/leer`,
     no leídos `GET /api/v1/chat/mensajes/conversacion/{idConversacion}/no-leidos`
   - **Tiempo real — CONTRATO STOMP EXACTO del backend (Spring WebSocket, NO Socket.IO):**
     - Usa **`@stomp/stompjs` con WebSocket NATIVO** (NO uses SockJS, el endpoint no lo expone):
       `new Client({ brokerURL: 'ws://localhost:8080/ws/chat', connectHeaders: { Authorization: 'Bearer <accessToken>' } })`.
       La autenticación va en el frame **CONNECT** con el header `Authorization: Bearer <token>`.
     - **Publicar (cliente → servidor, prefijo `/app`):**
       - `/app/chat.send` → body `{ idConversacion: UUID, tipo: "TEXTO", contenido: string (≤4000), adjunto?: {...}, idMensajeRespondido?: UUID }`
       - `/app/chat.read` → body `{ idConversacion: UUID }`
       - `/app/chat.typing` → body `{ idConversacion: UUID, escribiendo: boolean }`
       - `/app/chat.online` y `/app/chat.offline` → sin body (presencia)
     - **Suscribirse (servidor → cliente):**
       - `/user/queue/messages` → llega el **mensaje nuevo** (objeto mensaje completo) cuando alguien te escribe
       - `/user/queue/read` → acuse de lectura `{ idConversacion, idUsuario, fecha }`
       - `/user/queue/typing` → "escribiendo…" `{ idConversacion, idUsuario, escribiendo }`
       - `/topic/presence/{userId}` → estado de presencia (online/offline) de ese usuario
     - Nota: `/user/...` es la cola privada del usuario autenticado (Spring la enruta solo a sus sesiones).
       Suscríbete a `/user/queue/messages` (sin el id; Spring resuelve el usuario por el principal del token).
     - `socket.io-client` NO sirve aquí (protocolos incompatibles); inclúyelo solo si migras el backend.
   - **Fallback** siempre funcional si el WS falla: polling con TanStack Query (`refetchInterval`)
     sobre `GET /api/v1/chat/mensajes/conversacion/{idConversacion}`.

---

## ROL CLIENTE

7. **Dashboard Cliente**: resumen (servicios activos, pagos pendientes, accesos directos).
8. **Mi perfil**: ver `GET /api/v1/clientes/{idUsuario}`, editar `PATCH /api/v1/clientes/{idUsuario}`.
9. **Pedir un servicio** (crear): primero el cliente marca la ubicación en un **mapa Leaflet** y la
   registras con `POST /api/v1/ubicaciones` (body: departamento, provincia, distrito, direccionTexto,
   referencia?, latitud, longitud) → usa el `idUbicacion` devuelto para `POST /api/v1/services`
   (idUbicacion, titulo, descripcion, modalidad, prioridad, presupuestoMaximo?, fechaProgramada).
10. **Mis servicios**: `GET /api/v1/services/cliente/{idCliente}` (paginado), filtro por estado
    `GET /api/v1/services/estado/{estado}`, contador `GET /api/v1/services/cliente/{idCliente}/count`.
11. **Detalle de servicio**: `GET /api/v1/services/{idServicio}/detalle`. Permite cambiar estado
    `PATCH /api/v1/services/{idServicio}/estado`, editar `PUT /api/v1/services/{idServicio}`, borrar `DELETE`.
    El detalle **ya embebe** `cotizaciones[]`, `evidencias[]`, `calificacion` y los datos de la asignación
    (idAsignacion, idUsuarioTecnico, fechas) → NO requieren llamadas extra.
    **Ojo:** el detalle trae solo `idUbicacion` (no la dirección). Para mostrar la dirección haz un
    follow-up `GET /api/v1/ubicaciones/{idUbicacion}` y usa su `direccionTexto`.
12. **Cotizaciones recibidas** (dentro del detalle): `GET /api/v1/services/cotizaciones/servicio/{idServicio}/ordenadas`
    y `/pendientes`. El cliente responde/acepta: `PATCH /api/v1/services/cotizaciones/{idCotizacion}/responder`.
13. **Asignar técnico**: `POST /api/v1/services/asignaciones/{idServicio}/asignar`, editar `PUT .../{idAsignacion}`,
    cancelar `DELETE .../{idAsignacion}`. Seguir estado: `GET /api/v1/services/asignaciones/servicio/{idServicio}`.
14. **Evidencias del servicio**: ver `GET /api/v1/services/evidencias/servicio/{idServicio}`.
15. **Pagos**:
    - Métodos disponibles: `GET /api/v1/payments/methods`
    - Crear pago: `POST /api/v1/payments`; procesar: `POST /api/v1/payments/procesar`
    - Ver pago: `GET /api/v1/payments/{idPago}`; por servicio: `GET /api/v1/payments/servicio/{idServicio}`
    - Factura del pago: `GET /api/v1/payments/invoices/pago/{idPago}`, por código `.../codigo/{codigo}`
16. **Calificar al técnico** (tras finalizar): `POST /api/v1/services/calificaciones`
    (puntuación, puntualidad, profesionalismo, calidad, comunicación, ¿recomienda?).
17. **Buscar técnicos (estilo Airbnb: mapa + lista)** con **React Leaflet**: layout split — a un lado
    la **lista de cards** de técnicos (foto, ★ rating, especialidad, precio), al otro un mapa Leaflet
    con markers coral; al hover en una card resalta su marker. En móvil, lista arriba y mapa colapsable.
    Datos: por especialidad `GET /api/v1/technicians/buscar/especialidad`, por disponibilidad
    `GET /api/v1/technicians/buscar/disponibilidad`, por ubicación `GET /api/v1/technicians/location/{idUbicacion}/available-approved`,
    ver perfil `GET /api/v1/technicians/{id}/detalle`. Al pedir un servicio (pantalla 9) usa también
    un mapa Leaflet para **elegir la ubicación** del servicio.

---

## ROL TÉCNICO

18. **Dashboard Técnico**: estado de aprobación de su cuenta, disponibilidad (toggle), asignaciones activas,
    reputación, ingresos.
19. **Mi perfil técnico**: ver `GET /api/v1/technicians/{idUsuario}/detalle`, editar `PUT /api/v1/technicians/{idUsuario}`.
    Subir documentos de verificación: `PATCH /api/v1/technicians/{idUsuario}/documentos`.
    Toggle de disponibilidad: `PATCH /api/v1/technicians/{idUsuario}/disponibilidad`.
20. **Mis especialidades**: catálogo `GET /api/v1/technicians/specialties`, las mías
    `GET /api/v1/technicians/specialties/tecnico/{idUsuarioTecnico}`, asignar `POST .../asignar`,
    quitar `DELETE .../tecnico/{idTec}/especialidad/{idEsp}`.
21. **Mi agenda / horarios**: listar `GET /api/v1/technicians/schedules/tecnico/{idUsuarioTecnico}`,
    crear `POST /api/v1/technicians/schedules`, editar `PUT .../{idHorario}`, borrar `DELETE .../{idHorario}`.
    Excepciones (días libres): `GET/POST/DELETE /api/v1/technicians/schedule-exceptions...`.
22. **Servicios disponibles** (para cotizar): `GET /api/v1/services/disponibles` (paginado).
23. **Enviar cotización**: `POST /api/v1/services/cotizaciones`. Mis cotizaciones:
    `GET /api/v1/services/cotizaciones/tecnico/{idUsuarioTecnico}`. Cancelar `DELETE .../{idCotizacion}`.
24. **Mis asignaciones**: `GET /api/v1/services/asignaciones/tecnico/{idUsuarioTecnico}` (y `/count`),
    por estado `GET /api/v1/services/asignaciones/estado/{estado}`.
    **Iniciar trabajo**: `PATCH /api/v1/services/asignaciones/{idAsignacion}/iniciar`.
    **Finalizar trabajo**: `PATCH /api/v1/services/asignaciones/{idAsignacion}/finalizar`.
25. **Subir evidencias** del trabajo: `POST /api/v1/services/evidencias` (foto/archivo, tipo).
    Ver por servicio: `GET /api/v1/services/evidencias/servicio/{idServicio}`.
26. **Mi reputación y calificaciones**:
    - Reputación: `GET /api/v1/technicians/reputation/{idUsuarioTecnico}`
    - Calificaciones recibidas: `GET /api/v1/services/calificaciones/tecnico/{idUsuarioTecnico}`
    - Promedios: `.../promedio/puntuacion`, `/puntualidad`, `/profesionalismo`, `/calidad-trabajo`, `/comunicacion`
    - % recomendación: `.../porcentaje-recomendacion`
27. **Mi ubicación (mapa Leaflet)**: el técnico fija/ajusta su punto en un mapa React Leaflet y guarda
    con `PUT|PATCH /api/v1/technicians/{idUsuario}/location`.

---

# REQUISITOS DE CALIDAD
- **React 19** y TypeScript estricto; tipos/interfaces para cada respuesta de la API.
- **UI 100% con shadcn/ui** + Tailwind. Configura el theming de shadcn (tokens de color, radius).
  Apariencia premium tipo Airbnb/Uber/inDrive, no dashboard genérico.
- Estructura de carpetas clara: `src/api` (cliente axios + servicios por módulo),
  `src/features/<modulo>`, `src/components/ui` (shadcn), `src/components` (compuestos),
  `src/hooks`, `src/routes`, `src/stores` (Zustand), `src/lib`.
- **Estado global con Zustand** (store de auth con tokens + usuario + roles; persistido en localStorage).
- **TanStack Query** para todo el data fetching, con paginación, invalidación de caché tras mutaciones,
  estados de **loading (Skeleton), vacío y error** en cada pantalla.
- Formularios con **React Hook Form + Zod**, con los mismos límites del backend
  (clave 8-100, teléfono 10-20 dígitos, correo válido).
- Mapas con **React Leaflet**; realtime con **@stomp/stompjs** sobre WebSocket nativo (ver contrato de chat).
- Guarda de rutas por rol y redirecciones correctas.
- Un `README.md` con cómo correrlo (`npm i`, `npm run dev`, variable `VITE_API_BASE_URL`).

# ENTREGABLE
Genera el proyecto completo, archivo por archivo, listo para `npm install && npm run dev`.
Empieza por la configuración (Vite, Tailwind, init de **shadcn/ui**, cliente axios + interceptor de
refresh, router, store de auth con Zustand) y luego TODAS las pantallas listadas. No dejes pantallas
como "TODO": todas deben llamar a su endpoint real y verse pulidas.

### Dependencias esperadas (package.json)
`react@19`, `react-dom@19`, `react-router-dom`, `zustand`, `axios`, `@tanstack/react-query`,
`react-hook-form`, `zod`, `@hookform/resolvers`, `leaflet`, `react-leaflet`,
`@stomp/stompjs`, `lucide-react` + las que instale `shadcn/ui` (Radix, etc.).
(NO incluyas `socket.io-client` ni `sockjs-client`: incompatibles con el backend STOMP/WS nativo.)
