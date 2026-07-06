package com.intifix.modules.services.service.impl;

import com.intifix.modules.services.dto.request.CrearEvidenciaRequest;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.entity.EvidenciaServicio;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.enums.TipoArchivo;
import com.intifix.modules.services.exception.*;
import com.intifix.modules.services.mapper.EvidenciaMapper;
import com.intifix.modules.services.repository.EvidenciaServicioRepository;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.services.service.EvidenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for EvidenciaServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenciaServiceImpl implements EvidenciaService {

    private final EvidenciaServicioRepository evidenciaServicioRepository;
    private final ServicioRepository servicioRepository;
    private final EvidenciaMapper evidenciaMapper;

    @Override
    @Transactional
    public EvidenciaServicioResponse crearEvidencia(CrearEvidenciaRequest request) {
        log.info("Creando evidencia para servicio: {}", request.getIdServicio());

        servicioRepository.findById(request.getIdServicio())
            .orElseThrow(() -> {
                log.warn("Servicio no encontrado: {}", request.getIdServicio());
                return ServicioNoEncontradoException.byId(request.getIdServicio());
            });

        EvidenciaServicio evidencia = evidenciaMapper.toEntity(request);
        evidencia.setIdEvidencia(UUID.randomUUID());
        evidencia.setFechaSubida(java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault()));

        EvidenciaServicio guardada = evidenciaServicioRepository.save(evidencia);

        log.info("Evidencia creada exitosamente: {}", guardada.getIdEvidencia());
        return evidenciaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminarEvidencia(UUID idEvidencia) {
        log.info("Eliminando evidencia: {}", idEvidencia);

        EvidenciaServicio evidencia = evidenciaServicioRepository.findById(idEvidencia)
            .orElseThrow(() -> EvidenciaNoEncontradaException.byId(idEvidencia));

        evidenciaServicioRepository.delete(evidencia);
        log.info("Evidencia eliminada exitosamente: {}", idEvidencia);
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenciaServicioResponse obtenerEvidenciaPorId(UUID idEvidencia) {
        log.debug("Obteniendo evidencia por ID: {}", idEvidencia);

        EvidenciaServicio evidencia = evidenciaServicioRepository.findById(idEvidencia)
            .orElseThrow(() -> EvidenciaNoEncontradaException.byId(idEvidencia));

        return evidenciaMapper.toResponse(evidencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaServicioResponse> obtenerEvidenciasPorServicio(UUID idServicio) {
        log.debug("Obteniendo evidencias por servicio: {}", idServicio);
        List<EvidenciaServicio> evidencias = evidenciaServicioRepository.findByIdServicio(idServicio);
        return evidencias.stream()
            .map(evidenciaMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaServicioResponse> obtenerEvidenciasPorUsuario(UUID subidoPor) {
        log.debug("Obteniendo evidencias por usuario: {}", subidoPor);
        List<EvidenciaServicio> evidencias = evidenciaServicioRepository.findBySubidoPor(subidoPor);
        return evidencias.stream()
            .map(evidenciaMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaServicioResponse> obtenerEvidenciasPorServicioYTipo(UUID idServicio, TipoArchivo tipoArchivo) {
        log.debug("Obteniendo evidencias por servicio y tipo: {} - {}", idServicio, tipoArchivo);
        List<EvidenciaServicio> evidencias = evidenciaServicioRepository.findByIdServicioAndTipoArchivo(idServicio, tipoArchivo);
        return evidencias.stream()
            .map(evidenciaMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEvidenciasPorServicio(UUID idServicio) {
        log.debug("Contando evidencias por servicio: {}", idServicio);
        return evidenciaServicioRepository.countByIdServicio(idServicio);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEvidenciasPorUsuario(UUID subidoPor) {
        log.debug("Contando evidencias por usuario: {}", subidoPor);
        return evidenciaServicioRepository.countBySubidoPor(subidoPor);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEvidencia(UUID idEvidencia) {
        return evidenciaServicioRepository.existsById(idEvidencia);
    }
}
