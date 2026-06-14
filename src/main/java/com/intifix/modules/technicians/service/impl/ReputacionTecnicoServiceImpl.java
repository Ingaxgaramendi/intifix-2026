package com.intifix.modules.technicians.service.impl;

import com.intifix.modules.technicians.dto.response.ReputacionResponse;
import com.intifix.modules.technicians.entity.ReputacionTecnico;
import com.intifix.modules.technicians.exception.TecnicoNoEncontradoException;
import com.intifix.modules.technicians.mapper.ReputacionMapper;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import com.intifix.modules.technicians.repository.ReputacionTecnicoRepository;
import com.intifix.modules.technicians.service.ReputacionTecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReputacionTecnicoServiceImpl implements ReputacionTecnicoService {

    private final ReputacionTecnicoRepository reputacionTecnicoRepository;
    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final ReputacionMapper reputacionMapper;

    @Override
    @Transactional(readOnly = true)
    public ReputacionResponse obtenerReputacion(UUID idUsuarioTecnico) {
        log.debug("Obteniendo reputación para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al obtener reputación: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        // Lectura sin efectos secundarios: si aún no hay reputación persistida,
        // se responde la reputación neutra sin insertarla (la tx es readOnly).
        ReputacionTecnico reputacion = reputacionTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico)
            .orElseGet(() -> reputacionNeutra(idUsuarioTecnico));

        return reputacionMapper.toResponse(reputacion);
    }

    @Override
    @Transactional
    public ReputacionResponse actualizarReputacion(UUID idUsuarioTecnico, BigDecimal promedioCalificacion, Integer totalResenas) {
        log.info("Actualizando reputación para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al actualizar reputación: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        ReputacionTecnico reputacion = reputacionTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico)
            .orElseGet(() -> reputacionTecnicoRepository.save(reputacionNeutra(idUsuarioTecnico)));

        reputacion.setPromedioCalificacion(promedioCalificacion);
        reputacion.setTotalResenas(totalResenas);

        ReputacionTecnico actualizada = reputacionTecnicoRepository.save(reputacion);
        log.info("Reputación actualizada exitosamente para técnico idUsuario: {}", idUsuarioTecnico);

        return reputacionMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public ReputacionResponse incrementarServiciosCompletados(UUID idUsuarioTecnico) {
        log.info("Incrementando servicios completados para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al incrementar servicios: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        ReputacionTecnico reputacion = reputacionTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico)
            .orElseGet(() -> reputacionTecnicoRepository.save(reputacionNeutra(idUsuarioTecnico)));

        reputacion.setTotalServicios(reputacion.getTotalServicios() + 1);

        ReputacionTecnico actualizada = reputacionTecnicoRepository.save(reputacion);
        log.info("Servicios completados incrementados para técnico idUsuario: {}", idUsuarioTecnico);

        return reputacionMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public ReputacionResponse actualizarPromedioCalificaciones(UUID idUsuarioTecnico, BigDecimal nuevaCalificacion) {
        log.info("Actualizando promedio de calificaciones para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al actualizar promedio: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        ReputacionTecnico reputacion = reputacionTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico)
            .orElseGet(() -> reputacionTecnicoRepository.save(reputacionNeutra(idUsuarioTecnico)));

        int totalResenas = reputacion.getTotalResenas() + 1;
        BigDecimal promedioActual = reputacion.getPromedioCalificacion();

        BigDecimal nuevoPromedio = promedioActual.multiply(BigDecimal.valueOf(reputacion.getTotalResenas()))
            .add(nuevaCalificacion)
            .divide(BigDecimal.valueOf(totalResenas), 2, RoundingMode.HALF_UP);

        reputacion.setPromedioCalificacion(nuevoPromedio);
        reputacion.setTotalResenas(totalResenas);

        ReputacionTecnico actualizada = reputacionTecnicoRepository.save(reputacion);
        log.info("Promedio de calificaciones actualizado para técnico idUsuario: {}", idUsuarioTecnico);

        return reputacionMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public ReputacionResponse inicializarReputacion(UUID idUsuarioTecnico) {
        log.info("Inicializando reputación para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al inicializar reputación: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        if (reputacionTecnicoRepository.existsByIdUsuarioTecnico(idUsuarioTecnico)) {
            log.warn("Reputación ya existe para técnico idUsuario: {}", idUsuarioTecnico);
            ReputacionTecnico existente = reputacionTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico).get();
            return reputacionMapper.toResponse(existente);
        }

        ReputacionTecnico guardada = reputacionTecnicoRepository.save(reputacionNeutra(idUsuarioTecnico));
        log.info("Reputación inicializada exitosamente para técnico idUsuario: {}", idUsuarioTecnico);

        return reputacionMapper.toResponse(guardada);
    }

    private ReputacionTecnico reputacionNeutra(UUID idUsuarioTecnico) {
        return ReputacionTecnico.builder()
            .idUsuarioTecnico(idUsuarioTecnico)
            .promedioCalificacion(BigDecimal.ZERO)
            .totalResenas(0)
            .totalServicios(0)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeReputacion(UUID idUsuarioTecnico) {
        log.debug("Verificando existencia de reputación para técnico idUsuario: {}", idUsuarioTecnico);
        return reputacionTecnicoRepository.existsByIdUsuarioTecnico(idUsuarioTecnico);
    }
}
