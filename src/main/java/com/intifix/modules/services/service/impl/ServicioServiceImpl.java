package com.intifix.modules.services.service.impl;

import com.intifix.modules.services.dto.request.ActualizarServicioRequest;
import com.intifix.modules.services.dto.request.CambiarEstadoServicioRequest;
import com.intifix.modules.services.dto.request.CrearServicioRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.dto.response.CotizacionResponse;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.dto.response.ServicioDetalleResponse;
import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.entity.Cotizacion;
import com.intifix.modules.services.entity.HistorialServicio;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.EstadoCotizacion;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.enums.ModalidadServicio;
import com.intifix.modules.services.enums.TipoFecha;
import com.intifix.modules.services.enums.TipoSolicitud;
import com.intifix.modules.services.event.ServicioCreadoEvent;
import com.intifix.modules.audit.event.ServiceCreatedEvent;
import com.intifix.modules.audit.event.ServiceCancelledEvent;
import com.intifix.modules.services.exception.*;
import com.intifix.modules.services.gateway.GeolocationGateway;
import com.intifix.modules.services.gateway.UserGateway;
import com.intifix.modules.services.mapper.CalificacionMapper;
import com.intifix.modules.services.mapper.CotizacionMapper;
import com.intifix.modules.services.mapper.EvidenciaMapper;
import com.intifix.modules.services.mapper.ServicioDetalleMapper;
import com.intifix.modules.services.mapper.ServicioMapper;
import com.intifix.modules.services.repository.AsignacionServicioRepository;
import com.intifix.modules.services.repository.CalificacionRepository;
import com.intifix.modules.services.repository.CotizacionRepository;
import com.intifix.modules.services.repository.EvidenciaServicioRepository;
import com.intifix.modules.services.repository.HistorialServicioRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.services.service.ServicioService;
import com.intifix.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for Servicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioServiceImpl implements ServicioService {

    private static final String ROL_ADMIN = "ADMIN";
    private static final String ROL_TECNICO = "TECNICO";
    private static final String MSG_SERVICIO_NO_ENCONTRADO = "Servicio no encontrado: {}";

    private final ServicioRepository servicioRepository;
    private final HistorialServicioRepository historialServicioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final EvidenciaServicioRepository evidenciaServicioRepository;
    private final AsignacionServicioRepository asignacionServicioRepository;
    private final CalificacionRepository calificacionRepository;
    private final ServicioMapper servicioMapper;
    private final ServicioDetalleMapper servicioDetalleMapper;
    private final CotizacionMapper cotizacionMapper;
    private final EvidenciaMapper evidenciaMapper;
    private final CalificacionMapper calificacionMapper;
    private final UserGateway userGateway;
    private final GeolocationGateway geolocationGateway;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ServicioResponse crearServicio(CrearServicioRequest request) {
        UUID idCliente = SecurityUtils.currentUserId();
        log.info("Creando servicio para cliente autenticado: {}", idCliente);

        if (!userGateway.existsClient(idCliente)) {
            log.warn("Cliente no encontrado: {}", idCliente);
            throw ClienteNoEncontradoException.byId(idCliente);
        }

        // La ubicación solo aplica a servicios a domicilio. En taller del técnico
        // el cliente acude al taller, así que se ignora cualquier ubicación enviada.
        if (request.getModalidad() == ModalidadServicio.EN_CASA_CLIENTE) {
            if (request.getIdUbicacion() == null) {
                throw new IllegalArgumentException("La ubicación es obligatoria para servicios a domicilio.");
            }
            if (!geolocationGateway.existsLocation(request.getIdUbicacion())) {
                log.warn("Ubicación no encontrada: {}", request.getIdUbicacion());
                throw UbicacionNoEncontradaException.byId(request.getIdUbicacion());
            }
        } else {
            request.setIdUbicacion(null);
        }

        // Validate and normalise scheduling mode.
        TipoFecha tipoFecha = (request.getTipoFecha() != null) ? request.getTipoFecha() : TipoFecha.EXACTA;
        request.setTipoFecha(tipoFecha);
        switch (tipoFecha) {
            case EXACTA -> {
                if (request.getFechaProgramada() == null)
                    throw new IllegalArgumentException("La fecha programada es obligatoria para el modo exacto.");
                if (!request.getFechaProgramada().isAfter(ZonedDateTime.now()))
                    throw new IllegalArgumentException("La fecha programada debe ser futura.");
            }
            case RANGO -> {
                if (request.getFechaInicioRango() == null || request.getFechaFinRango() == null)
                    throw new IllegalArgumentException("El rango de fechas es obligatorio.");
                if (!request.getFechaInicioRango().isAfter(ZonedDateTime.now()))
                    throw new IllegalArgumentException("La fecha de inicio del rango debe ser futura.");
                if (!request.getFechaFinRango().isAfter(request.getFechaInicioRango()))
                    throw new IllegalArgumentException("La fecha fin debe ser posterior al inicio.");
                long dias = ChronoUnit.DAYS.between(
                    request.getFechaInicioRango().toLocalDate(),
                    request.getFechaFinRango().toLocalDate()
                );
                if (dias > 5)
                    throw new IllegalArgumentException("El rango de fechas no puede superar los 5 días.");
            }
            case URGENTE -> {
                // No scheduled date; clear any accidental value sent by the client.
                request.setFechaProgramada(null);
                request.setFechaInicioRango(null);
                request.setFechaFinRango(null);
            }
        }

        Servicio servicio = servicioMapper.toEntity(request);
        servicio.setIdCliente(idCliente);
        servicio.setEstado(EstadoServicio.PENDIENTE);

        TipoSolicitud tipo = (request.getTipoSolicitud() != null) ? request.getTipoSolicitud() : TipoSolicitud.PUBLICA;
        servicio.setTipoSolicitud(tipo);
        if (tipo == TipoSolicitud.DIRECTA) {
            if (request.getIdTecnicoDirecto() == null) {
                throw new IllegalArgumentException("Debe indicar el técnico para una solicitud directa.");
            }
            servicio.setIdTecnicoDirecto(request.getIdTecnicoDirecto());
        }
        // UUID will be generated by database via Persistable<UUID>

        Servicio guardado = servicioRepository.save(servicio);
        
        registrarHistorial(guardado.getIdServicio(), null, EstadoServicio.PENDIENTE, "Servicio creado", idCliente);
        
        eventPublisher.publishEvent(new ServicioCreadoEvent(
            guardado.getIdServicio(),
            guardado.getIdCliente(),
            guardado.getIdUbicacion(),
            guardado.getTitulo(),
            guardado.getFechaCreacion()
        ));

        // Auditoría desacoplada del alta de servicio.
        eventPublisher.publishEvent(new ServiceCreatedEvent(
            guardado.getIdServicio(),
            guardado.getIdCliente()
        ));

        log.info("Servicio creado exitosamente: {}", guardado.getIdServicio());
        return servicioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ServicioResponse actualizarServicio(UUID idServicio, ActualizarServicioRequest request) {
        log.info("Actualizando servicio: {}", idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn(MSG_SERVICIO_NO_ENCONTRADO, idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        verificarPropiedadOAdmin(servicio);

        if (servicio.getEstado() == EstadoServicio.FINALIZADO) {
            log.warn("Intento de actualizar servicio finalizado: {}", idServicio);
            throw ServicioNoModificableException.finalized(idServicio);
        }

        servicioMapper.updateEntityFromDto(request, servicio);
        Servicio actualizado = servicioRepository.save(servicio);

        log.info("Servicio actualizado exitosamente: {}", actualizado.getIdServicio());
        return servicioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public ServicioResponse cambiarEstadoServicio(UUID idServicio, CambiarEstadoServicioRequest request) {
        log.info("Cambiando estado del servicio: {} a {}", idServicio, request.getEstado());

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn(MSG_SERVICIO_NO_ENCONTRADO, idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        verificarPropiedadOAdmin(servicio);

        EstadoServicio estadoAnterior = servicio.getEstado();
        EstadoServicio estadoNuevo = request.getEstado();

        if (estadoAnterior == EstadoServicio.FINALIZADO) {
            log.warn("Intento de cambiar estado de servicio finalizado: {}", idServicio);
            throw ServicioNoModificableException.finalized(idServicio);
        }

        validarTransicion(idServicio, estadoAnterior, estadoNuevo);

        servicio.setEstado(estadoNuevo);

        if (estadoNuevo == EstadoServicio.FINALIZADO) {
            servicio.setFechaFinalizacion(java.time.ZonedDateTime.now());
        }

        Servicio actualizado = servicioRepository.save(servicio);

        registrarHistorial(
            actualizado.getIdServicio(),
            estadoAnterior,
            estadoNuevo,
            request.getComentario(),
            SecurityUtils.currentUserId()
        );

        if (estadoNuevo == EstadoServicio.CANCELADO) {
            eventPublisher.publishEvent(new ServiceCancelledEvent(
                actualizado.getIdServicio(),
                SecurityUtils.currentUserId(),
                request.getComentario()
            ));
        }

        log.info("Estado del servicio cambiado exitosamente: {} de {} a {}",
            idServicio, estadoAnterior, estadoNuevo);
        return servicioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminarServicio(UUID idServicio) {
        log.info("Eliminando servicio: {}", idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn(MSG_SERVICIO_NO_ENCONTRADO, idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        verificarPropiedadOAdmin(servicio);

        if (servicio.getEstado() != EstadoServicio.PENDIENTE) {
            log.warn("Intento de eliminar servicio no pendiente: {}", idServicio);
            throw ServicioNoEliminableException.notPending(idServicio);
        }

        // Aunque siga PENDIENTE, si ya recibió cotizaciones no se borra (hubo
        // trabajo de técnicos): debe cancelarse para conservar la trazabilidad.
        if (cotizacionRepository.existsByIdServicio(idServicio)) {
            log.warn("Intento de eliminar servicio con cotizaciones: {}", idServicio);
            throw ServicioNoEliminableException.hasCotizaciones(idServicio);
        }

        servicioRepository.delete(servicio);
        log.info("Servicio eliminado exitosamente: {}", idServicio);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse obtenerServicioPorId(UUID idServicio) {
        log.debug("Obteniendo servicio por ID: {}", idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn(MSG_SERVICIO_NO_ENCONTRADO, idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        verificarAccesoLectura(servicio);

        return servicioMapper.toResponse(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioDetalleResponse obtenerDetalleServicioPorId(UUID idServicio) {
        log.debug("Obteniendo detalle de servicio por ID: {}", idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn(MSG_SERVICIO_NO_ENCONTRADO, idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        verificarAccesoLectura(servicio);

        AsignacionServicio asignacion = asignacionServicioRepository
            .findByIdServicio(idServicio).orElse(null);

        List<CotizacionResponse> cotizaciones = cotizacionRepository
            .findByIdServicio(idServicio, Pageable.unpaged())
            .getContent().stream()
            .map(cotizacionMapper::toResponse)
            .toList();

        List<EvidenciaServicioResponse> evidencias = evidenciaServicioRepository
            .findByIdServicio(idServicio).stream()
            .map(evidenciaMapper::toResponse)
            .toList();

        CalificacionResponse calificacion = calificacionRepository
            .findByIdServicio(idServicio)
            .map(calificacionMapper::toResponse)
            .orElse(null);

        return servicioDetalleMapper.toDetalleResponse(servicio, asignacion, cotizaciones, evidencias, calificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> obtenerServiciosPorCliente(UUID idCliente, Pageable pageable) {
        log.debug("Obteniendo servicios por cliente: {}", idCliente);
        Page<Servicio> servicios = servicioRepository.findByIdCliente(idCliente, pageable);
        return servicios.map(servicioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> obtenerServiciosPorUbicacion(UUID idUbicacion, Pageable pageable) {
        log.debug("Obteniendo servicios por ubicación: {}", idUbicacion);
        Page<Servicio> servicios = servicioRepository.findByIdUbicacion(idUbicacion, pageable);
        return servicios.map(servicioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> obtenerServiciosPorEstado(EstadoServicio estado, Pageable pageable) {
        log.debug("Obteniendo servicios por estado: {}", estado);
        Page<Servicio> servicios = servicioRepository.findByEstado(estado, pageable);
        return servicios.map(servicioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> obtenerServiciosDisponibles(Pageable pageable) {
        log.debug("Obteniendo servicios disponibles (marketplace)");
        Page<Servicio> servicios = servicioRepository.findByEstadoInAndTipoSolicitud(
            List.of(EstadoServicio.PENDIENTE, EstadoServicio.COTIZANDO), TipoSolicitud.PUBLICA, pageable);
        // Enriquecer con el nombre del cliente; cache por página para no repetir
        // la consulta cuando un mismo cliente tiene varios servicios.
        Map<UUID, String> nombrePorCliente = new HashMap<>();
        return servicios.map(servicio -> {
            ServicioResponse resp = servicioMapper.toResponse(servicio);
            UUID idCliente = servicio.getIdCliente();
            if (idCliente != null) {
                resp.setNombreCliente(
                    nombrePorCliente.computeIfAbsent(idCliente, userGateway::getClientName));
            }
            return resp;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> obtenerSolicitudesDirectas(Pageable pageable) {
        UUID idTecnico = SecurityUtils.currentUserId();
        log.debug("Obteniendo solicitudes directas para técnico: {}", idTecnico);
        Page<Servicio> servicios = servicioRepository.findByIdTecnicoDirectoAndEstadoIn(
            idTecnico, List.of(EstadoServicio.PENDIENTE, EstadoServicio.COTIZANDO), pageable);
        Map<UUID, String> nombrePorCliente = new HashMap<>();
        return servicios.map(s -> {
            ServicioResponse resp = servicioMapper.toResponse(s);
            if (s.getIdCliente() != null) {
                resp.setNombreCliente(nombrePorCliente.computeIfAbsent(s.getIdCliente(), userGateway::getClientName));
            }
            return resp;
        });
    }

    @Override
    @Transactional
    public ServicioResponse aceptarSolicitudDirecta(UUID idServicio) {
        UUID idTecnico = SecurityUtils.currentUserId();
        log.info("Técnico {} aceptando solicitud directa {}", idTecnico, idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> ServicioNoEncontradoException.byId(idServicio));

        if (servicio.getTipoSolicitud() != TipoSolicitud.DIRECTA) {
            throw new IllegalStateException("El servicio no es una solicitud directa.");
        }
        if (!idTecnico.equals(servicio.getIdTecnicoDirecto())) {
            throw new org.springframework.security.access.AccessDeniedException("No eres el técnico de esta solicitud directa.");
        }
        if (servicio.getEstado() != EstadoServicio.PENDIENTE && servicio.getEstado() != EstadoServicio.COTIZANDO) {
            throw ServicioNoModificableException.finalized(idServicio);
        }
        if (asignacionServicioRepository.existsByIdServicio(idServicio)) {
            throw new IllegalStateException("El servicio ya tiene un técnico asignado.");
        }

        // Auto-cotización: usa el presupuesto máximo del cliente; 0.01 si no definió.
        java.math.BigDecimal precio = servicio.getPresupuestoMaximo() != null
            ? servicio.getPresupuestoMaximo()
            : java.math.BigDecimal.valueOf(0.01);

        Cotizacion cotizacion = Cotizacion.builder()
            .idServicio(idServicio)
            .idUsuarioTecnico(idTecnico)
            .precio(precio)
            .tiempoEstimado("Por definir")
            .comentario("Solicitud directa aceptada")
            .estado(EstadoCotizacion.ACEPTADA)
            .build();
        cotizacion = cotizacionRepository.save(cotizacion);

        AsignacionServicio asignacion = AsignacionServicio.builder()
            .idAsignacion(UUID.randomUUID())
            .idServicio(idServicio)
            .idUsuarioTecnico(idTecnico)
            .idCotizacion(cotizacion.getIdCotizacion())
            .estadoServicio(EstadoServicio.ASIGNADO)
            .fechaAsignacion(java.time.ZonedDateTime.now())
            .build();
        asignacionServicioRepository.save(asignacion);

        EstadoServicio estadoAnterior = servicio.getEstado();
        servicio.setEstado(EstadoServicio.ASIGNADO);
        Servicio actualizado = servicioRepository.save(servicio);

        registrarHistorial(idServicio, estadoAnterior,
            EstadoServicio.ASIGNADO, "Solicitud directa aceptada por el técnico", idTecnico);

        log.info("Solicitud directa {} aceptada por técnico {}", idServicio, idTecnico);
        return servicioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public ServicioResponse rechazarSolicitudDirecta(UUID idServicio) {
        UUID idTecnico = SecurityUtils.currentUserId();
        log.info("Técnico {} rechazando solicitud directa {}", idTecnico, idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> ServicioNoEncontradoException.byId(idServicio));

        if (servicio.getTipoSolicitud() != TipoSolicitud.DIRECTA) {
            throw new IllegalStateException("El servicio no es una solicitud directa.");
        }
        if (!idTecnico.equals(servicio.getIdTecnicoDirecto())) {
            throw new org.springframework.security.access.AccessDeniedException("No eres el técnico de esta solicitud directa.");
        }

        // El servicio vuelve al marketplace general.
        servicio.setTipoSolicitud(TipoSolicitud.PUBLICA);
        servicio.setIdTecnicoDirecto(null);
        Servicio actualizado = servicioRepository.save(servicio);

        registrarHistorial(idServicio, null, servicio.getEstado(),
            "Solicitud directa rechazada; publicada en marketplace", idTecnico);

        log.info("Solicitud directa {} rechazada; ahora es pública", idServicio);
        return servicioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> buscarServiciosPorTitulo(String titulo, Pageable pageable) {
        log.debug("Buscando servicios por título: {}", titulo);
        Page<Servicio> servicios = servicioRepository.buscarPorTitulo(titulo, pageable);
        return servicios.map(servicioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarServiciosPorCliente(UUID idCliente) {
        log.debug("Contando servicios por cliente: {}", idCliente);
        return servicioRepository.countByIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarServiciosPorEstado(EstadoServicio estado) {
        log.debug("Contando servicios por estado: {}", estado);
        return servicioRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeServicio(UUID idServicio) {
        return servicioRepository.existsById(idServicio);
    }

    /**
     * Autoriza operaciones de mutación: solo ADMIN o el cliente dueño del servicio.
     * Lanza {@link AccessDeniedException} (403) en caso contrario.
     */
    private void verificarPropiedadOAdmin(Servicio servicio) {
        if (SecurityUtils.tieneRol(ROL_ADMIN)) {
            return;
        }
        if (!SecurityUtils.esPropietario(servicio.getIdCliente())) {
            log.warn("Acceso denegado al servicio {} para usuario {}",
                servicio.getIdServicio(), SecurityUtils.currentUserId());
            throw new AccessDeniedException("No tiene permiso sobre este servicio");
        }
    }

    /**
     * Autoriza lectura por id: ADMIN y TECNICO ven cualquier servicio (marketplace);
     * un CLIENTE solo puede leer los suyos.
     */
    private void verificarAccesoLectura(Servicio servicio) {
        if (SecurityUtils.tieneRol(ROL_ADMIN) || SecurityUtils.tieneRol(ROL_TECNICO)) {
            return;
        }
        if (!SecurityUtils.esPropietario(servicio.getIdCliente())) {
            log.warn("Lectura denegada del servicio {} para usuario {}",
                servicio.getIdServicio(), SecurityUtils.currentUserId());
            throw new AccessDeniedException("No tiene permiso sobre este servicio");
        }
    }

    /**
     * Máquina de estados. ADMIN puede ejecutar cualquier transición (la salida
     * desde FINALIZADO ya está bloqueada antes). Un CLIENTE dueño solo puede
     * cancelar, y únicamente desde estados tempranos del ciclo de vida.
     */
    private void validarTransicion(UUID idServicio, EstadoServicio desde, EstadoServicio hacia) {
        if (SecurityUtils.tieneRol(ROL_ADMIN)) {
            return;
        }
        boolean clienteCancelaPermitido = hacia == EstadoServicio.CANCELADO
            && (desde == EstadoServicio.PENDIENTE
                || desde == EstadoServicio.COTIZANDO
                || desde == EstadoServicio.ASIGNADO);
        if (!clienteCancelaPermitido) {
            log.warn("Transición no permitida para CLIENTE en servicio {}: {} -> {}",
                idServicio, desde, hacia);
            throw ServicioNoModificableException.transicionInvalida(idServicio, desde, hacia);
        }
    }

    private void registrarHistorial(UUID idServicio, EstadoServicio estadoAnterior,
                                   EstadoServicio estadoNuevo, String comentario, UUID cambiadoPor) {
        HistorialServicio historial = HistorialServicio.builder()
            .idHistorial(UUID.randomUUID())
            .idServicio(idServicio)
            .estadoAnterior(estadoAnterior)
            .estadoNuevo(estadoNuevo)
            .comentario(comentario)
            .cambiadoPor(cambiadoPor)
            .fechaCambio(java.time.ZonedDateTime.now())
            .build();
        
        historialServicioRepository.save(historial);
    }
}
