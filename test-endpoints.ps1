# ===========================================================================
# Smoke test de TODOS los endpoints del backend Intifix.
# Registra usuarios CLIENTE/TECNICO/ADMIN, crea datos en cadena y golpea
# cada endpoint registrando el status HTTP. Uso: .\test-endpoints.ps1
# ===========================================================================
$base = "http://localhost:8080"
$results = New-Object System.Collections.Generic.List[object]
$ts = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$rid = [guid]::NewGuid().ToString()   # id aleatorio para rutas sin dato real

function Req {
  param($mod,$name,$method,$path,$token,$bodyJson)
  $headers=@{}
  if($token){ $headers["Authorization"]="Bearer $token" }
  $code=$null;$data=$null
  try {
    $p=@{Method=$method;Uri="$base$path";Headers=$headers;UseBasicParsing=$true;TimeoutSec=25}
    if($bodyJson){ $p.Body=$bodyJson; $p.ContentType="application/json" }
    $r=Invoke-WebRequest @p
    $code=[int]$r.StatusCode
    try { $data=($r.Content | ConvertFrom-Json).data } catch {}
  } catch {
    if($_.Exception.Response){ try{$code=[int]$_.Exception.Response.StatusCode}catch{$code=-1} } else {$code=-1}
  }
  $results.Add([pscustomobject]@{Modulo=$mod;Endpoint=$name;Method=$method;Status=$code})
  return $data
}

Write-Host "== Setup: registrando usuarios ==" -ForegroundColor Cyan
$cliBody='{"correo":"cli'+$ts+'@test.com","clave":"Password123","telefono":"9876543210","dni":"10000001","roles":["CLIENTE"]}'
$tecBody='{"correo":"tec'+$ts+'@test.com","clave":"Password123","telefono":"9876543211","dni":"10000002","roles":["TECNICO"]}'
$admBody='{"correo":"adm'+$ts+'@test.com","clave":"Password123","telefono":"9876543212","dni":"10000003","roles":["ADMIN"]}'
$thrBody='{"correo":"thr'+$ts+'@test.com","clave":"Password123","telefono":"9876543213","dni":"10000004","roles":["CLIENTE"]}'

$cli = Req "AUTH" "POST /auth/register (cliente)" POST "/api/v1/auth/register" $null $cliBody
$tec = Req "AUTH" "POST /auth/register (tecnico)" POST "/api/v1/auth/register" $null $tecBody
$adm = Req "AUTH" "POST /auth/register (admin)"   POST "/api/v1/auth/register" $null $admBody
$thr = Req "AUTH" "POST /auth/register (throwaway)" POST "/api/v1/auth/register" $null $thrBody

$cliTok=$cli.accessToken; $tecTok=$tec.accessToken; $admTok=$adm.accessToken; $thrTok=$thr.accessToken
$thrRefresh=$thr.refreshToken

# login (registra el endpoint)
$loginBody='{"correo":"cli'+$ts+'@test.com","clave":"Password123"}'
$null = Req "AUTH" "POST /auth/login" POST "/api/v1/auth/login" $null $loginBody

# ids de usuario
$cliMe = Req "AUTH" "GET /auth/current-user" GET "/api/v1/auth/current-user" $cliTok $null
$tecMe = Req "AUTH" "GET /auth/current-user (tec)" GET "/api/v1/auth/current-user" $tecTok $null
$cliId = $cliMe.idUsuario; $tecId = $tecMe.idUsuario
$null = Req "AUTH" "GET /auth/validate-session" GET "/api/v1/auth/validate-session" $cliTok $null
if($cli.refreshToken){ $null = Req "AUTH" "POST /auth/refresh" POST "/api/v1/auth/refresh" $null ('{"refreshToken":"'+$cli.refreshToken+'"}') }

Write-Host "cliId=$cliId tecId=$tecId" -ForegroundColor DarkGray

Write-Host "== Setup: creando datos en cadena ==" -ForegroundColor Cyan
# Perfil cliente
$null = Req "CLIENTES" "POST /clientes" POST "/api/v1/clientes" $cliTok ('{"idUsuario":"'+$cliId+'","nombresCompletos":"Cliente Test","dniRuc":"12345678"}')
# Perfil tecnico (puede requerir ADMIN; probamos con tec y luego admin)
$ubic = Req "UBICACIONES" "POST /ubicaciones" POST "/api/v1/ubicaciones" $cliTok '{"departamento":"Lima","provincia":"Lima","distrito":"Miraflores","direccionTexto":"Av Pardo 123","referencia":"ref","latitud":-12.1211,"longitud":-77.0290}'
$ubicId = $ubic.idUbicacion
$tecProfile = Req "TECNICOS" "POST /technicians" POST "/api/v1/technicians" $admTok ('{"idUsuario":"'+$tecId+'","nombresCompletos":"Tecnico Test","dniRuc":"12345678","experienciaAnios":5,"estadoAprobacion":"APROBADO","disponibilidad":"DISPONIBLE","tarifaBase":50.00,"idUbicacion":"'+$ubicId+'"}')
# Especialidad
$esp = Req "ESPECIALIDADES" "POST /specialties" POST "/api/v1/technicians/specialties" $admTok '{"nombre":"Gasfiteria Test","descripcion":"desc"}'
$espId = $esp.idEspecialidad
# Servicio
$serv = Req "SERVICIOS" "POST /services" POST "/api/v1/services" $cliTok ('{"idUbicacion":"'+$ubicId+'","idEspecialidad":"'+$espId+'","titulo":"Reparacion de cano","descripcion":"Se rompio un cano en la cocina","modalidad":"EN_CASA_CLIENTE","prioridad":"MEDIA","presupuestoMaximo":250.00,"fechaProgramada":"2026-12-20T10:00:00Z"}')
$servId = $serv.idServicio
# Cotizacion
$cot = Req "COTIZACIONES" "POST /cotizaciones" POST "/api/v1/services/cotizaciones" $tecTok ('{"idServicio":"'+$servId+'","precio":150.00,"tiempoEstimado":"2 horas","comentario":"puedo ir manana"}')
$cotId = $cot.idCotizacion
# Horario
$hor = Req "HORARIOS" "POST /schedules" POST "/api/v1/technicians/schedules" $tecTok ('{"idUsuarioTecnico":"'+$tecId+'","diaSemana":1,"horaInicio":"08:00","horaFin":"17:00","activo":true}')
$horId = $hor.idHorario
# Conversacion
$conv = Req "CHAT" "POST /chat/conversaciones" POST "/api/v1/chat/conversaciones" $cliTok ('{"idServicio":"'+$servId+'"}')
$convId = $conv.idConversacion
# Metodo de pago (ADMIN)
$mp = Req "PAGOS" "POST /payments/methods" POST "/api/v1/payments/methods" $admTok '{"nombre":"Tarjeta Test"}'
$mpId = $mp.idMetodoPago

function Pick($v){ if($v){return $v}else{return $rid} }
$ubicId=Pick $ubicId; $espId=Pick $espId; $servId=Pick $servId; $cotId=Pick $cotId; $horId=Pick $horId; $convId=Pick $convId; $mpId=Pick $mpId

Write-Host "== Recorriendo todos los endpoints ==" -ForegroundColor Cyan

# ---- AUTH (resto) ----
$null = Req "AUTH" "GET /clientes/existe/{id}" GET "/api/v1/clientes/existe/$cliId" $cliTok $null

# ---- UBICACIONES ----
$null = Req "UBICACIONES" "GET /ubicaciones/{id}" GET "/api/v1/ubicaciones/$ubicId" $cliTok $null

# ---- CLIENTES ----
$null = Req "CLIENTES" "GET /clientes/{id}" GET "/api/v1/clientes/$cliId" $cliTok $null
$null = Req "CLIENTES" "GET /clientes/{id}/detalle" GET "/api/v1/clientes/$cliId/detalle" $admTok $null
$null = Req "CLIENTES" "PATCH /clientes/{id}" PATCH "/api/v1/clientes/$cliId" $cliTok '{"nombresCompletos":"Cliente Editado"}'
$null = Req "CLIENTES" "GET /clientes" GET "/api/v1/clientes?page=0&size=5" $admTok $null
$null = Req "CLIENTES" "GET /clientes/buscar" GET "/api/v1/clientes/buscar?page=0&size=5" $admTok $null
$null = Req "CLIENTES" "GET /clientes/buscar/dni" GET "/api/v1/clientes/buscar/dni?dni=12345678" $admTok $null
$null = Req "CLIENTES" "GET /clientes/existe/dni" GET "/api/v1/clientes/existe/dni?dni=12345678" $admTok $null
$null = Req "CLIENTES" "GET /clientes/total" GET "/api/v1/clientes/total" $admTok $null
$null = Req "CLIENTES" "DELETE /clientes/{id} (random)" DELETE "/api/v1/clientes/$rid" $admTok $null

# ---- TECNICOS ----
$null = Req "TECNICOS" "GET /technicians/{id}" GET "/api/v1/technicians/$tecId" $tecTok $null
$null = Req "TECNICOS" "GET /technicians/{id}/detalle" GET "/api/v1/technicians/$tecId/detalle" $tecTok $null
$null = Req "TECNICOS" "PUT /technicians/{id}" PUT "/api/v1/technicians/$tecId" $tecTok '{"nombresCompletos":"Tec Editado"}'
$null = Req "TECNICOS" "GET /technicians" GET "/api/v1/technicians?page=0&size=5" $admTok $null
$null = Req "TECNICOS" "GET /technicians/buscar/dni" GET "/api/v1/technicians/buscar/dni?dni=12345678" $admTok $null
$null = Req "TECNICOS" "GET /technicians/buscar/disponibilidad" GET "/api/v1/technicians/buscar/disponibilidad?disponibilidad=DISPONIBLE&page=0&size=5" $admTok $null
$null = Req "TECNICOS" "GET /technicians/buscar/especialidad" GET "/api/v1/technicians/buscar/especialidad?idEspecialidad=$espId&page=0&size=5" $cliTok $null
$null = Req "TECNICOS" "GET /technicians/buscar/estado" GET "/api/v1/technicians/buscar/estado?estadoAprobacion=APROBADO&page=0&size=5" $admTok $null
$null = Req "TECNICOS" "PATCH /technicians/{id}/aprobar" PATCH "/api/v1/technicians/$tecId/aprobar" $admTok $null
$null = Req "TECNICOS" "PATCH /technicians/{id}/disponibilidad" PATCH "/api/v1/technicians/$tecId/disponibilidad" $tecTok '{"disponibilidad":"OCUPADO"}'
$null = Req "TECNICOS" "PATCH /technicians/{id}/documentos" PATCH "/api/v1/technicians/$tecId/documentos" $tecTok '{"dniFrontalUrl":"https://x.com/a.jpg"}'
$null = Req "TECNICOS" "GET /technicians/existe/{id}" GET "/api/v1/technicians/existe/$tecId" $cliTok $null
$null = Req "TECNICOS" "GET /technicians/existe/dni" GET "/api/v1/technicians/existe/dni?dni=12345678" $admTok $null
$null = Req "TECNICOS" "GET /technicians/total" GET "/api/v1/technicians/total" $admTok $null
$null = Req "TECNICOS" "GET /technicians/total/aprobados" GET "/api/v1/technicians/total/aprobados" $admTok $null
$null = Req "TECNICOS" "GET /technicians/total/activos" GET "/api/v1/technicians/total/activos" $admTok $null
$null = Req "TECNICOS" "PUT /technicians/{id}/location" PUT "/api/v1/technicians/$tecId/location?idUbicacion=$ubicId" $tecTok $null
$null = Req "TECNICOS" "PATCH /technicians/{id}/location" PATCH "/api/v1/technicians/$tecId/location?idUbicacion=$ubicId" $tecTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}" GET "/api/v1/technicians/location/$ubicId?page=0&size=5" $admTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/available" GET "/api/v1/technicians/location/$ubicId/available?page=0&size=5" $cliTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/approved" GET "/api/v1/technicians/location/$ubicId/approved?page=0&size=5" $cliTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/available-approved" GET "/api/v1/technicians/location/$ubicId/available-approved?page=0&size=5" $cliTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/count" GET "/api/v1/technicians/location/$ubicId/count" $admTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/count/available" GET "/api/v1/technicians/location/$ubicId/count/available" $admTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/count/approved" GET "/api/v1/technicians/location/$ubicId/count/approved" $admTok $null
$null = Req "TECNICOS" "GET /technicians/location/{id}/count/available-approved" GET "/api/v1/technicians/location/$ubicId/count/available-approved" $admTok $null

# ---- ESPECIALIDADES ----
$null = Req "ESPECIALIDADES" "GET /specialties" GET "/api/v1/technicians/specialties?page=0&size=5" $cliTok $null
$null = Req "ESPECIALIDADES" "GET /specialties/{id}" GET "/api/v1/technicians/specialties/$espId" $cliTok $null
$null = Req "ESPECIALIDADES" "PUT /specialties/{id}" PUT "/api/v1/technicians/specialties/$espId" $admTok '{"descripcion":"nueva desc"}'
$null = Req "ESPECIALIDADES" "GET /specialties/nombre/{n}" GET "/api/v1/technicians/specialties/nombre/Gasfiteria%20Test" $cliTok $null
$null = Req "ESPECIALIDADES" "POST /specialties/asignar" POST "/api/v1/technicians/specialties/asignar" $admTok ('{"idUsuarioTecnico":"'+$tecId+'","idEspecialidad":"'+$espId+'"}')
$null = Req "ESPECIALIDADES" "GET /specialties/tecnico/{id}" GET "/api/v1/technicians/specialties/tecnico/$tecId" $cliTok $null
$null = Req "ESPECIALIDADES" "GET /specialties/{id}/tecnicos" GET "/api/v1/technicians/specialties/$espId/tecnicos" $cliTok $null
$null = Req "ESPECIALIDADES" "GET /specialties/existe/{id}" GET "/api/v1/technicians/specialties/existe/$espId" $cliTok $null
$null = Req "ESPECIALIDADES" "GET /specialties/existe/nombre" GET "/api/v1/technicians/specialties/existe/nombre?nombre=Gasfiteria%20Test" $cliTok $null
$null = Req "ESPECIALIDADES" "DELETE /specialties/tecnico/{t}/especialidad/{e}" DELETE "/api/v1/technicians/specialties/tecnico/$tecId/especialidad/$espId" $admTok $null

# ---- HORARIOS ----
$null = Req "HORARIOS" "GET /schedules/tecnico/{id}" GET "/api/v1/technicians/schedules/tecnico/$tecId" $tecTok $null
$null = Req "HORARIOS" "GET /schedules/{id}" GET "/api/v1/technicians/schedules/$horId" $tecTok $null
$null = Req "HORARIOS" "PUT /schedules/{id}" PUT "/api/v1/technicians/schedules/$horId" $tecTok '{"activo":false}'
$null = Req "HORARIOS" "DELETE /schedules/{id}" DELETE "/api/v1/technicians/schedules/$horId" $tecTok $null

# ---- EXCEPCIONES HORARIO ----
$exc = Req "EXCEPCIONES" "POST /schedule-exceptions" POST "/api/v1/technicians/schedule-exceptions" $tecTok ('{"idUsuarioTecnico":"'+$tecId+'","fechaInicio":"2026-12-24T00:00:00Z","fechaFin":"2026-12-26T00:00:00Z","motivo":"vacaciones navidad"}')
$excId = Pick $exc.idExcepcion
$null = Req "EXCEPCIONES" "GET /schedule-exceptions/tecnico/{id}" GET "/api/v1/technicians/schedule-exceptions/tecnico/$tecId" $tecTok $null
$null = Req "EXCEPCIONES" "GET /schedule-exceptions/{id}" GET "/api/v1/technicians/schedule-exceptions/$excId" $tecTok $null
$null = Req "EXCEPCIONES" "DELETE /schedule-exceptions/{id}" DELETE "/api/v1/technicians/schedule-exceptions/$excId" $tecTok $null

# ---- REPUTACION ----
$null = Req "REPUTACION" "POST /reputation/{id}/inicializar" POST "/api/v1/technicians/reputation/$tecId/inicializar" $admTok $null
$null = Req "REPUTACION" "GET /reputation/{id}" GET "/api/v1/technicians/reputation/$tecId" $cliTok $null
$null = Req "REPUTACION" "GET /reputation/{id}/existe" GET "/api/v1/technicians/reputation/$tecId/existe" $cliTok $null
$null = Req "REPUTACION" "PATCH /reputation/{id}/actualizar" PATCH "/api/v1/technicians/reputation/$tecId/actualizar" $admTok $null
$null = Req "REPUTACION" "PATCH /reputation/{id}/incrementar-servicios" PATCH "/api/v1/technicians/reputation/$tecId/incrementar-servicios" $admTok $null

# ---- SERVICIOS ----
$null = Req "SERVICIOS" "GET /services/{id}" GET "/api/v1/services/$servId" $cliTok $null
$null = Req "SERVICIOS" "GET /services/{id}/detalle" GET "/api/v1/services/$servId/detalle" $cliTok $null
$null = Req "SERVICIOS" "PUT /services/{id}" PUT "/api/v1/services/$servId" $cliTok '{"titulo":"Reparacion urgente cano"}'
$null = Req "SERVICIOS" "GET /services/cliente/{id}" GET "/api/v1/services/cliente/$cliId?page=0&size=5" $cliTok $null
$null = Req "SERVICIOS" "GET /services/cliente/{id}/count" GET "/api/v1/services/cliente/$cliId/count" $cliTok $null
$null = Req "SERVICIOS" "GET /services/disponibles" GET "/api/v1/services/disponibles?page=0&size=5" $tecTok $null
$null = Req "SERVICIOS" "GET /services/ubicacion/{id}" GET "/api/v1/services/ubicacion/$ubicId?page=0&size=5" $cliTok $null
$null = Req "SERVICIOS" "GET /services/estado/{e}" GET "/api/v1/services/estado/PENDIENTE?page=0&size=5" $admTok $null
$null = Req "SERVICIOS" "GET /services/estado/{e}/count" GET "/api/v1/services/estado/PENDIENTE/count" $admTok $null
$null = Req "SERVICIOS" "GET /services/buscar" GET "/api/v1/services/buscar?page=0&size=5" $admTok $null

# ---- COTIZACIONES ----
$null = Req "COTIZACIONES" "GET /cotizaciones/{id}" GET "/api/v1/services/cotizaciones/$cotId" $cliTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/servicio/{id}" GET "/api/v1/services/cotizaciones/servicio/$servId?page=0&size=5" $cliTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/servicio/{id}/pendientes" GET "/api/v1/services/cotizaciones/servicio/$servId/pendientes?page=0&size=5" $cliTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/servicio/{id}/ordenadas" GET "/api/v1/services/cotizaciones/servicio/$servId/ordenadas?page=0&size=5" $cliTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/servicio/{id}/count" GET "/api/v1/services/cotizaciones/servicio/$servId/count" $cliTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/tecnico/{id}" GET "/api/v1/services/cotizaciones/tecnico/$tecId?page=0&size=5" $tecTok $null
$null = Req "COTIZACIONES" "GET /cotizaciones/tecnico/{id}/count" GET "/api/v1/services/cotizaciones/tecnico/$tecId/count" $tecTok $null
$null = Req "COTIZACIONES" "PATCH /cotizaciones/{id}/responder" PATCH "/api/v1/services/cotizaciones/$cotId/responder" $cliTok '{"estado":"ACEPTADA"}'
$null = Req "COTIZACIONES" "POST /cotizaciones/expirar-vencidas" POST "/api/v1/services/cotizaciones/expirar-vencidas" $admTok $null

# ---- ASIGNACIONES ----
$asig = Req "ASIGNACIONES" "POST /asignaciones/{idServ}/asignar" POST "/api/v1/services/asignaciones/$servId/asignar" $cliTok ('{"idUsuarioTecnico":"'+$tecId+'","idCotizacion":"'+$cotId+'"}')
$asigId = Pick $asig.idAsignacion
$null = Req "ASIGNACIONES" "GET /asignaciones/{id}" GET "/api/v1/services/asignaciones/$asigId" $cliTok $null
$null = Req "ASIGNACIONES" "GET /asignaciones/servicio/{id}" GET "/api/v1/services/asignaciones/servicio/$servId" $cliTok $null
$null = Req "ASIGNACIONES" "GET /asignaciones/tecnico/{id}" GET "/api/v1/services/asignaciones/tecnico/$tecId" $tecTok $null
$null = Req "ASIGNACIONES" "GET /asignaciones/tecnico/{id}/count" GET "/api/v1/services/asignaciones/tecnico/$tecId/count" $tecTok $null
$null = Req "ASIGNACIONES" "GET /asignaciones/estado/{e}" GET "/api/v1/services/asignaciones/estado/ASIGNADO" $tecTok $null
$null = Req "ASIGNACIONES" "GET /asignaciones/estado/{e}/count" GET "/api/v1/services/asignaciones/estado/ASIGNADO/count" $tecTok $null
$null = Req "ASIGNACIONES" "PUT /asignaciones/{id}" PUT "/api/v1/services/asignaciones/$asigId" $cliTok ('{"idUsuarioTecnico":"'+$tecId+'","idCotizacion":"'+$cotId+'","notasTecnico":"ok"}')
$null = Req "ASIGNACIONES" "PATCH /asignaciones/{id}/iniciar" PATCH "/api/v1/services/asignaciones/$asigId/iniciar" $tecTok $null
$null = Req "ASIGNACIONES" "PATCH /asignaciones/{id}/finalizar" PATCH "/api/v1/services/asignaciones/$asigId/finalizar" $tecTok $null

# ---- EVIDENCIAS ----
$evi = Req "EVIDENCIAS" "POST /evidencias" POST "/api/v1/services/evidencias" $tecTok ('{"idServicio":"'+$servId+'","urlArchivo":"https://x.com/foto.jpg","nombreArchivo":"foto.jpg","tipoArchivo":"IMAGEN","subidoPor":"'+$tecId+'"}')
$eviId = Pick $evi.idEvidencia
$null = Req "EVIDENCIAS" "GET /evidencias/{id}" GET "/api/v1/services/evidencias/$eviId" $cliTok $null
$null = Req "EVIDENCIAS" "GET /evidencias/servicio/{id}" GET "/api/v1/services/evidencias/servicio/$servId" $cliTok $null
$null = Req "EVIDENCIAS" "GET /evidencias/servicio/{id}/count" GET "/api/v1/services/evidencias/servicio/$servId/count" $cliTok $null
$null = Req "EVIDENCIAS" "GET /evidencias/usuario/{id}" GET "/api/v1/services/evidencias/usuario/$tecId" $tecTok $null
$null = Req "EVIDENCIAS" "GET /evidencias/usuario/{id}/count" GET "/api/v1/services/evidencias/usuario/$tecId/count" $tecTok $null
$null = Req "EVIDENCIAS" "GET /evidencias/servicio/{id}/tipo/{t}" GET "/api/v1/services/evidencias/servicio/$servId/tipo/IMAGEN" $cliTok $null
$null = Req "EVIDENCIAS" "DELETE /evidencias/{id}" DELETE "/api/v1/services/evidencias/$eviId" $tecTok $null

# ---- CALIFICACIONES ----
$cal = Req "CALIFICACIONES" "POST /calificaciones" POST "/api/v1/services/calificaciones" $cliTok ('{"idServicio":"'+$servId+'","puntuacion":5,"comentario":"excelente","puntualidad":5,"profesionalismo":5,"calidadTrabajo":5,"comunicacion":5,"recomendaria":true}')
$calId = Pick $cal.idCalificacion
$null = Req "CALIFICACIONES" "GET /calificaciones/{id}" GET "/api/v1/services/calificaciones/$calId" $cliTok $null
$null = Req "CALIFICACIONES" "GET /calificaciones/servicio/{id}" GET "/api/v1/services/calificaciones/servicio/$servId" $cliTok $null
$null = Req "CALIFICACIONES" "GET /calificaciones/tecnico/{id}" GET "/api/v1/services/calificaciones/tecnico/$tecId?page=0&size=5" $cliTok $null
$null = Req "CALIFICACIONES" "GET /calificaciones/cliente/{id}" GET "/api/v1/services/calificaciones/cliente/$cliId?page=0&size=5" $cliTok $null
$null = Req "CALIFICACIONES" "GET /calificaciones/tecnico/{id}/count" GET "/api/v1/services/calificaciones/tecnico/$tecId/count" $cliTok $null
$null = Req "CALIFICACIONES" "GET /calificaciones/cliente/{id}/count" GET "/api/v1/services/calificaciones/cliente/$cliId/count" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../promedio/puntuacion" GET "/api/v1/services/calificaciones/tecnico/$tecId/promedio/puntuacion" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../promedio/puntualidad" GET "/api/v1/services/calificaciones/tecnico/$tecId/promedio/puntualidad" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../promedio/profesionalismo" GET "/api/v1/services/calificaciones/tecnico/$tecId/promedio/profesionalismo" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../promedio/calidad-trabajo" GET "/api/v1/services/calificaciones/tecnico/$tecId/promedio/calidad-trabajo" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../promedio/comunicacion" GET "/api/v1/services/calificaciones/tecnico/$tecId/promedio/comunicacion" $cliTok $null
$null = Req "CALIFICACIONES" "GET .../porcentaje-recomendacion" GET "/api/v1/services/calificaciones/tecnico/$tecId/porcentaje-recomendacion" $cliTok $null

# ---- REPORTES ----
$rep = Req "REPORTES" "POST /reportes" POST "/api/v1/services/reportes" $cliTok ('{"idServicio":"'+$servId+'","idReportado":"'+$tecId+'","tipoReporte":"CONDUCTA","motivo":"motivo de prueba largo"}')

# ---- PAGOS ----
$pay = Req "PAGOS" "POST /payments" POST "/api/v1/payments" $cliTok ('{"idServicio":"'+$servId+'","idMetodoPago":"'+$mpId+'","montoTotal":250.00,"comisionPlataforma":25.00,"montoNetoTecnico":200.00,"impuestoTotal":25.00}')
$payId = Pick $pay.idPago
$null = Req "PAGOS" "POST /payments/procesar" POST "/api/v1/payments/procesar" $cliTok ('{"idPago":"'+$payId+'","descripcion":"pago test"}')
$null = Req "PAGOS" "GET /payments/{id}" GET "/api/v1/payments/$payId" $cliTok $null
$null = Req "PAGOS" "GET /payments/servicio/{id}" GET "/api/v1/payments/servicio/$servId" $cliTok $null
$null = Req "PAGOS" "GET /payments" GET "/api/v1/payments?page=0&size=5" $admTok $null
$null = Req "PAGOS" "GET /payments/estado/{e}" GET "/api/v1/payments/estado/PENDIENTE?page=0&size=5" $admTok $null
$null = Req "PAGOS" "GET /payments/resumen" GET "/api/v1/payments/resumen" $admTok $null
$null = Req "PAGOS" "POST /payments/{id}/confirmar" POST "/api/v1/payments/$payId/confirmar" $admTok ('{"idPago":"'+$payId+'","transactionId":"tx-123"}')
$null = Req "PAGOS" "POST /payments/reembolsar" POST "/api/v1/payments/reembolsar" $admTok ('{"idPago":"'+$payId+'","razon":"motivo de reembolso de prueba"}')
$null = Req "PAGOS" "GET /payments/methods" GET "/api/v1/payments/methods" $cliTok $null
$null = Req "PAGOS" "GET /payments/methods/{id}" GET "/api/v1/payments/methods/$mpId" $cliTok $null
$null = Req "PAGOS" "GET /payments/methods/nombre/{n}" GET "/api/v1/payments/methods/nombre/Tarjeta%20Test" $cliTok $null

# ---- FACTURAS ----
$fac = Req "FACTURAS" "POST /payments/invoices" POST "/api/v1/payments/invoices" $admTok ('{"idPago":"'+$payId+'","tipo":"BOLETA"}')
$facId = Pick $fac.idFactura
$null = Req "FACTURAS" "GET /payments/invoices/{id}" GET "/api/v1/payments/invoices/$facId" $admTok $null
$null = Req "FACTURAS" "GET /payments/invoices/pago/{id}" GET "/api/v1/payments/invoices/pago/$payId" $admTok $null
$null = Req "FACTURAS" "GET /payments/invoices" GET "/api/v1/payments/invoices?page=0&size=5" $admTok $null
$null = Req "FACTURAS" "GET /payments/invoices/estado/{e}" GET "/api/v1/payments/invoices/estado/EMITIDA?page=0&size=5" $admTok $null

# ---- CHAT ----
$null = Req "CHAT" "GET /chat/conversaciones" GET "/api/v1/chat/conversaciones?page=0&size=5" $cliTok $null
$null = Req "CHAT" "GET /chat/conversaciones/{id}" GET "/api/v1/chat/conversaciones/$convId" $cliTok $null
$msg = Req "CHAT" "POST /chat/mensajes" POST "/api/v1/chat/mensajes" $cliTok ('{"idConversacion":"'+$convId+'","tipo":"TEXTO","contenido":"hola tecnico"}')
$msgId = Pick $msg.idMensaje
$null = Req "CHAT" "GET /chat/mensajes/conversacion/{id}" GET "/api/v1/chat/mensajes/conversacion/$convId?page=0&size=5" $cliTok $null
$null = Req "CHAT" "GET /chat/mensajes/conversacion/{id}/no-leidos" GET "/api/v1/chat/mensajes/conversacion/$convId/no-leidos" $cliTok $null
$null = Req "CHAT" "POST /chat/mensajes/conversacion/{id}/leer" POST "/api/v1/chat/mensajes/conversacion/$convId/leer" $tecTok $null
$null = Req "CHAT" "PUT /chat/mensajes/{id}" PUT "/api/v1/chat/mensajes/$msgId" $cliTok '{"contenido":"hola editado"}'
$null = Req "CHAT" "PATCH /chat/conversaciones/{id}/archivar" PATCH "/api/v1/chat/conversaciones/$convId/archivar" $cliTok $null
$null = Req "CHAT" "PATCH /chat/conversaciones/{id}/bloquear" PATCH "/api/v1/chat/conversaciones/$convId/bloquear" $cliTok $null
$null = Req "CHAT" "DELETE /chat/mensajes/{id}" DELETE "/api/v1/chat/mensajes/$msgId" $cliTok $null
$null = Req "CHAT" "DELETE /chat/conversaciones/{id}" DELETE "/api/v1/chat/conversaciones/$convId" $cliTok $null

# ---- NOTIFICACIONES ----
$null = Req "NOTIFICACIONES" "GET /notifications" GET "/api/v1/notifications?page=0&size=5" $cliTok $null
$null = Req "NOTIFICACIONES" "GET /notifications/no-leidas" GET "/api/v1/notifications/no-leidas?page=0&size=5" $cliTok $null
$null = Req "NOTIFICACIONES" "GET /notifications/contador" GET "/api/v1/notifications/contador" $cliTok $null
$null = Req "NOTIFICACIONES" "PATCH /notifications/leer-todas" PATCH "/api/v1/notifications/leer-todas" $cliTok $null
$null = Req "NOTIFICACIONES" "PATCH /notifications/{id}/leer" PATCH "/api/v1/notifications/$rid/leer" $cliTok $null
$null = Req "NOTIFICACIONES" "DELETE /notifications/{id}" DELETE "/api/v1/notifications/$rid" $cliTok $null

# ---- AI ----
$null = Req "AI" "POST /ai/chat" POST "/api/v1/ai/chat" $cliTok ('{"conversationId":"conv-'+$ts+'","userId":"'+$cliId+'","message":"hola"}')

# ---- AUDIT (ADMIN) ----
$null = Req "AUDIT" "GET /admin/audit/events" GET "/api/admin/audit/events?page=0&size=5" $admTok $null
$null = Req "AUDIT" "GET /admin/security/events" GET "/api/admin/security/events?page=0&size=5" $admTok $null
$null = Req "AUDIT" "GET /admin/exceptions" GET "/api/admin/exceptions?page=0&size=5" $admTok $null
$null = Req "AUDIT" "GET /admin/http-logs" GET "/api/admin/http-logs?page=0&size=5" $admTok $null
$null = Req "AUDIT" "GET /admin/websocket-logs" GET "/api/admin/websocket-logs?page=0&size=5" $admTok $null
$null = Req "AUDIT" "GET /admin/geo-logs" GET "/api/admin/geo-logs?page=0&size=5" $admTok $null

# ---- AUTH logout (al final, con throwaway) ----
if($thrRefresh){ $null = Req "AUTH" "POST /auth/logout" POST "/api/v1/auth/logout" $thrTok ('{"refreshToken":"'+$thrRefresh+'"}') }

# ===================== RESUMEN =====================
Write-Host "`n================ RESULTADOS ================" -ForegroundColor Green
$results | Format-Table Modulo,Method,Endpoint,Status -AutoSize | Out-String -Width 200 | Write-Host

$ok    = ($results | Where-Object { $_.Status -ge 200 -and $_.Status -lt 300 }).Count
$auth  = ($results | Where-Object { $_.Status -eq 401 -or $_.Status -eq 403 }).Count
$bad   = ($results | Where-Object { $_.Status -eq 400 -or $_.Status -eq 404 -or $_.Status -eq 409 }).Count
$err   = ($results | Where-Object { $_.Status -ge 500 }).Count
$noresp= ($results | Where-Object { $_.Status -eq -1 }).Count
Write-Host "`n== RESUMEN ==" -ForegroundColor Green
Write-Host "Total llamadas : $($results.Count)"
Write-Host "2xx (OK)       : $ok" -ForegroundColor Green
Write-Host "400/404/409    : $bad" -ForegroundColor Yellow
Write-Host "401/403 (auth) : $auth" -ForegroundColor Yellow
Write-Host "5xx (ERROR)    : $err" -ForegroundColor Red
Write-Host "Sin respuesta  : $noresp" -ForegroundColor Red
Write-Host "`n== 5xx / sin respuesta (a revisar) ==" -ForegroundColor Red
$results | Where-Object { $_.Status -ge 500 -or $_.Status -eq -1 } | Format-Table Modulo,Method,Endpoint,Status -AutoSize | Out-String -Width 200 | Write-Host


