package com.intifix.modules.services.service.impl;

import com.intifix.modules.services.dto.request.CrearCalificacionRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.entity.Calificacion;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.event.CalificacionRegistradaEvent;
import com.intifix.modules.services.exception.*;
import com.intifix.modules.services.mapper.CalificacionMapper;
import com.intifix.modules.services.repository.AsignacionServicioRepository;
import com.intifix.modules.services.repository.CalificacionRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.services.service.CalificacionService;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for Calificacion operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ServicioRepository servicioRepository;
    private final AsignacionServicioRepository asignacionRepository;
    private final CalificacionMapper calificacionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CalificacionResponse crearCalificacion(CrearCalificacionRequest request) {
        UUID idCliente = SecurityUtils.currentUserId();
        log.info("Creando calificación para servicio: {} por cliente autenticado: {}", 
            request.getIdServicio(), idCliente);

        Servicio servicio = servicioRepository.findById(request.getIdServicio())
            .orElseThrow(() -> {
                log.warn("Servicio no encontrado: {}", request.getIdServicio());
                return ServicioNoEncontradoException.byId(request.getIdServicio());
            });

        if (!servicio.getIdCliente().equals(idCliente)) {
            log.warn("Cliente {} no es propietario del servicio {}", idCliente, request.getIdServicio());
            throw new AccessDeniedException("Solo el propietario del servicio puede calificarlo");
        }

        if (servicio.getEstado() != EstadoServicio.FINALIZADO) {
            log.warn("Intento de calificar servicio no finalizado: {}", request.getIdServicio());
            throw ServicioFinalizadoException.byId(request.getIdServicio());
        }

        if (calificacionRepository.existsByIdServicio(request.getIdServicio())) {
            log.warn("Calificación duplicada para servicio: {}", request.getIdServicio());
            throw CalificacionDuplicadaException.byService(request.getIdServicio());
        }

        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            log.warn("Puntuación inválida: {}", request.getPuntuacion());
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }

        AsignacionServicio asignacion = asignacionRepository.findByIdServicio(request.getIdServicio())
            .orElseThrow(() -> {
                log.warn("No existe asignación para servicio: {}", request.getIdServicio());
                return AsignacionNoEncontradaException.byServicio(request.getIdServicio());
            });
        UUID idUsuarioTecnico = asignacion.getIdUsuarioTecnico();

        Calificacion calificacion = calificacionMapper.toEntity(request);
        calificacion.setIdCalificacion(UUID.randomUUID());
        calificacion.setIdCliente(idCliente);
        calificacion.setIdUsuarioTecnico(idUsuarioTecnico);
        calificacion.setFechaCalificacion(java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault()));

        Calificacion guardada = calificacionRepository.save(calificacion);

        eventPublisher.publishEvent(new CalificacionRegistradaEvent(
            guardada.getIdCalificacion(),
            guardada.getIdServicio(),
            guardada.getIdUsuarioTecnico(),
            guardada.getIdCliente(),
            guardada.getPuntuacion(),
            guardada.getComentario(),
            guardada.getFechaCalificacion()
        ));

        log.info("Calificación creada exitosamente: {}", guardada.getIdCalificacion());
        return calificacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminarCalificacion(UUID idCalificacion) {
        log.info("Eliminando calificación: {}", idCalificacion);

        Calificacion calificacion = calificacionRepository.findById(idCalificacion)
            .orElseThrow(() -> CalificacionNoEncontradaException.byId(idCalificacion));

        calificacionRepository.delete(calificacion);
        log.info("Calificación eliminada exitosamente: {}", idCalificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerCalificacionPorId(UUID idCalificacion) {
        log.debug("Obteniendo calificación por ID: {}", idCalificacion);

        Calificacion calificacion = calificacionRepository.findById(idCalificacion)
            .orElseThrow(() -> CalificacionNoEncontradaException.byId(idCalificacion));

        return calificacionMapper.toResponse(calificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerCalificacionPorServicio(UUID idServicio) {
        log.debug("Obteniendo calificación por servicio: {}", idServicio);

        return calificacionRepository.findByIdServicio(idServicio)
            .map(calificacionMapper::toResponse)
            .orElseThrow(() -> CalificacionNoEncontradaException.byServicio(idServicio));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CalificacionResponse> obtenerCalificacionesPorTecnico(UUID idUsuarioTecnico, Pageable pageable) {
        log.debug("Obteniendo calificaciones por técnico: {}", idUsuarioTecnico);
        Page<Calificacion> calificaciones = calificacionRepository.findByIdUsuarioTecnico(idUsuarioTecnico, pageable);
        return calificaciones.map(calificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CalificacionResponse> obtenerCalificacionesPorCliente(UUID idCliente, Pageable pageable) {
        log.debug("Obteniendo calificaciones por cliente: {}", idCliente);
        Page<Calificacion> calificaciones = calificacionRepository.findByIdCliente(idCliente, pageable);
        return calificaciones.map(calificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioPuntuacionTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo promedio de puntuación para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.averagePuntuacionByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioPuntualidadTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo promedio de puntualidad para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.averagePuntualidadByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioProfesionalismoTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo promedio de profesionalismo para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.averageProfesionalismoByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioCalidadTrabajoTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo promedio de calidad de trabajo para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.averageCalidadTrabajoByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioComunicacionTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo promedio de comunicación para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.averageComunicacionByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPorcentajeRecomendacionTecnico(UUID idUsuarioTecnico) {
        log.debug("Obteniendo porcentaje de recomendación para técnico: {}", idUsuarioTecnico);
        return calificacionRepository.porcentajeRecomendacionByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarCalificacionesPorTecnico(UUID idUsuarioTecnico) {
        log.debug("Contando calificaciones por técnico: {}", idUsuarioTecnico);
        return calificacionRepository.countByIdUsuarioTecnico(idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarCalificacionesPorCliente(UUID idCliente) {
        log.debug("Contando calificaciones por cliente: {}", idCliente);
        return calificacionRepository.countByIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCalificacion(UUID idCalificacion) {
        return calificacionRepository.existsById(idCalificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCalificacionPorServicio(UUID idServicio) {
        return calificacionRepository.existsByIdServicio(idServicio);
    }
}
