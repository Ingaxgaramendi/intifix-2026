package com.intifix.modules.services.service.impl;

import com.intifix.modules.services.dto.request.AsignarTecnicoRequest;
import com.intifix.modules.services.dto.response.AsignacionServicioResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.entity.Cotizacion;
import com.intifix.modules.services.entity.HistorialServicio;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.EstadoCotizacion;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.event.ServicioAsignadoEvent;
import com.intifix.modules.services.exception.*;
import com.intifix.modules.services.mapper.AsignacionServicioMapper;
import com.intifix.modules.services.repository.AsignacionServicioRepository;
import com.intifix.modules.services.repository.CotizacionRepository;
import com.intifix.modules.services.repository.HistorialServicioRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.services.gateway.UserGateway;
import com.intifix.modules.services.service.AsignacionServicioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for AsignacionServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsignacionServicioServiceImpl implements AsignacionServicioService {

    private final AsignacionServicioRepository asignacionServicioRepository;
    private final ServicioRepository servicioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final HistorialServicioRepository historialServicioRepository;
    private final AsignacionServicioMapper asignacionServicioMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UserGateway userGateway;

    @Override
    @Transactional
    public AsignacionServicioResponse asignarTecnico(UUID idServicio, AsignarTecnicoRequest request) {
        log.info("Asignando técnico {} al servicio {}", request.getIdUsuarioTecnico(), idServicio);

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> {
                log.warn("Servicio no encontrado: {}", idServicio);
                return ServicioNoEncontradoException.byId(idServicio);
            });

        if (asignacionServicioRepository.existsByIdServicio(idServicio)) {
            log.warn("Servicio ya asignado: {}", idServicio);
            throw ServicioYaAsignadoException.byId(idServicio);
        }

        Cotizacion cotizacion = cotizacionRepository.findById(request.getIdCotizacion())
            .orElseThrow(() -> CotizacionNoEncontradaException.byId(request.getIdCotizacion()));

        if (cotizacion.getEstado() != EstadoCotizacion.ACEPTADA) {
            log.warn("Cotización no aceptada: {}", request.getIdCotizacion());
            throw CotizacionNoModificableException.accepted(request.getIdCotizacion());
        }

        AsignacionServicio asignacion = asignacionServicioMapper.toEntity(request);
        asignacion.setIdAsignacion(UUID.randomUUID());
        asignacion.setIdServicio(idServicio);
        asignacion.setEstadoServicio(EstadoServicio.ASIGNADO);
        asignacion.setFechaAsignacion(java.time.ZonedDateTime.now());

        AsignacionServicio guardada = asignacionServicioRepository.save(asignacion);

        servicio.setEstado(EstadoServicio.ASIGNADO);
        servicioRepository.save(servicio);

        registrarHistorial(idServicio, EstadoServicio.COTIZANDO, EstadoServicio.ASIGNADO, 
            "Técnico asignado", request.getIdUsuarioTecnico());

        eventPublisher.publishEvent(new ServicioAsignadoEvent(
            guardada.getIdServicio(),
            guardada.getIdAsignacion(),
            guardada.getIdUsuarioTecnico(),
            servicio.getIdCliente(),
            guardada.getIdCotizacion(),
            guardada.getFechaAsignacion()
        ));

        log.info("Técnico asignado exitosamente: {}", guardada.getIdAsignacion());
        return asignacionServicioMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public AsignacionServicioResponse actualizarAsignacion(UUID idAsignacion, AsignarTecnicoRequest request) {
        log.info("Actualizando asignación: {}", idAsignacion);

        AsignacionServicio asignacion = asignacionServicioRepository.findById(idAsignacion)
            .orElseThrow(() -> AsignacionNoEncontradaException.byId(idAsignacion));

        asignacionServicioMapper.updateEntityFromDto(request, asignacion);
        AsignacionServicio actualizada = asignacionServicioRepository.save(asignacion);

        log.info("Asignación actualizada exitosamente: {}", actualizada.getIdAsignacion());
        return asignacionServicioMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminarAsignacion(UUID idAsignacion) {
        log.info("Eliminando asignación: {}", idAsignacion);

        AsignacionServicio asignacion = asignacionServicioRepository.findById(idAsignacion)
            .orElseThrow(() -> AsignacionNoEncontradaException.byId(idAsignacion));

        Servicio servicio = servicioRepository.findById(asignacion.getIdServicio())
            .orElseThrow(() -> ServicioNoEncontradoException.byId(asignacion.getIdServicio()));

        if (asignacion.getEstadoServicio() == EstadoServicio.EN_PROCESO) {
            throw AsignacionNoModificableException.inProgress(idAsignacion);
        }

        asignacionServicioRepository.delete(asignacion);

        servicio.setEstado(EstadoServicio.COTIZANDO);
        servicioRepository.save(servicio);

        log.info("Asignación eliminada exitosamente: {}", idAsignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionServicioResponse obtenerAsignacionPorId(UUID idAsignacion) {
        log.debug("Obteniendo asignación por ID: {}", idAsignacion);

        AsignacionServicio asignacion = asignacionServicioRepository.findById(idAsignacion)
            .orElseThrow(() -> AsignacionNoEncontradaException.byId(idAsignacion));

        return asignacionServicioMapper.toResponse(asignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionServicioResponse obtenerAsignacionPorServicio(UUID idServicio) {
        log.debug("Obteniendo asignación por servicio: {}", idServicio);

        return asignacionServicioRepository.findByIdServicio(idServicio)
            .map(asignacionServicioMapper::toResponse)
            .orElseThrow(() -> AsignacionNoEncontradaException.byServicio(idServicio));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsignacionServicioResponse> obtenerAsignacionesPorTecnico(UUID idUsuarioTecnico, Pageable pageable) {
        log.debug("Obteniendo asignaciones por técnico: {}", idUsuarioTecnico);
        Map<UUID, String> nombreCache = new HashMap<>();
        return asignacionServicioRepository
            .findByIdUsuarioTecnico(idUsuarioTecnico, pageable)
            .map(a -> {
                AsignacionServicioResponse resp = asignacionServicioMapper.toResponse(a);
                servicioRepository.findById(a.getIdServicio()).ifPresent(s -> {
                    resp.setTituloServicio(s.getTitulo());
                    resp.setIdCliente(s.getIdCliente());
                    if (s.getIdCliente() != null) {
                        resp.setNombreCliente(
                            nombreCache.computeIfAbsent(s.getIdCliente(), userGateway::getClientName));
                    }
                });
                return resp;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionServicioResponse> obtenerAsignacionesPorEstado(EstadoServicio estado) {
        log.debug("Obteniendo asignaciones por estado: {}", estado);
        List<AsignacionServicio> asignaciones = asignacionServicioRepository.findByEstadoServicio(estado);
        return asignaciones.stream()
            .map(asignacionServicioMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public void iniciarServicio(UUID idAsignacion) {
        log.info("Iniciando servicio: {}", idAsignacion);

        AsignacionServicio asignacion = asignacionServicioRepository.findById(idAsignacion)
            .orElseThrow(() -> AsignacionNoEncontradaException.byId(idAsignacion));

        Servicio servicio = servicioRepository.findById(asignacion.getIdServicio())
            .orElseThrow(() -> ServicioNoEncontradoException.byId(asignacion.getIdServicio()));

        asignacion.setEstadoServicio(EstadoServicio.EN_PROCESO);
        asignacion.setFechaInicioReal(java.time.ZonedDateTime.now());
        asignacionServicioRepository.save(asignacion);

        servicio.setEstado(EstadoServicio.EN_PROCESO);
        servicioRepository.save(servicio);

        registrarHistorial(asignacion.getIdServicio(), EstadoServicio.ASIGNADO, EstadoServicio.EN_PROCESO,
            "Servicio iniciado", asignacion.getIdUsuarioTecnico());

        log.info("Servicio iniciado exitosamente: {}", idAsignacion);
    }

    @Override
    @Transactional
    public void finalizarServicio(UUID idAsignacion) {
        log.info("Finalizando servicio: {}", idAsignacion);

        AsignacionServicio asignacion = asignacionServicioRepository.findById(idAsignacion)
            .orElseThrow(() -> AsignacionNoEncontradaException.byId(idAsignacion));

        Servicio servicio = servicioRepository.findById(asignacion.getIdServicio())
            .orElseThrow(() -> ServicioNoEncontradoException.byId(asignacion.getIdServicio()));

        asignacion.setEstadoServicio(EstadoServicio.FINALIZADO);
        asignacion.setFechaFinReal(java.time.ZonedDateTime.now());
        asignacionServicioRepository.save(asignacion);

        servicio.setEstado(EstadoServicio.FINALIZADO);
        servicio.setFechaFinalizacion(java.time.ZonedDateTime.now());
        servicioRepository.save(servicio);

        registrarHistorial(asignacion.getIdServicio(), EstadoServicio.EN_PROCESO, EstadoServicio.FINALIZADO,
            "Servicio finalizado", asignacion.getIdUsuarioTecnico());

        log.info("Servicio finalizado exitosamente: {}", idAsignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarAsignacionesPorTecnico(UUID idUsuarioTecnico) {
        log.debug("Contando asignaciones por técnico: {}", idUsuarioTecnico);
        return asignacionServicioRepository.countByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarAsignacionesPorEstado(EstadoServicio estado) {
        log.debug("Contando asignaciones por estado: {}", estado);
        return asignacionServicioRepository.countByEstadoServicio(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAsignacionPorServicio(UUID idServicio) {
        return asignacionServicioRepository.existsByIdServicio(idServicio);
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
