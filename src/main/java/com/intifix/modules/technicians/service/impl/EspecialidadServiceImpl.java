package com.intifix.modules.technicians.service.impl;

import com.intifix.modules.technicians.dto.request.ActualizarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.AsignarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.CrearEspecialidadRequest;
import com.intifix.modules.technicians.dto.response.EspecialidadResponse;
import com.intifix.modules.technicians.dto.response.EspecialidadTecnicoResponse;
import com.intifix.modules.technicians.entity.Especialidad;
import com.intifix.modules.technicians.entity.TecnicoEspecialidad;
import com.intifix.modules.technicians.exception.EspecialidadNoEncontradaException;
import com.intifix.modules.technicians.exception.TecnicoNoEncontradoException;
import com.intifix.modules.technicians.mapper.EspecialidadMapper;
import com.intifix.modules.technicians.repository.EspecialidadRepository;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import com.intifix.modules.technicians.repository.TecnicoEspecialidadRepository;
import com.intifix.modules.technicians.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final TecnicoEspecialidadRepository tecnicoEspecialidadRepository;
    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final EspecialidadMapper especialidadMapper;

    @Override
    @Transactional
    public EspecialidadResponse crearEspecialidad(CrearEspecialidadRequest request) {
        log.info("Creando especialidad con nombre: {}", request.getNombre());

        if (especialidadRepository.existsByNombre(request.getNombre())) {
            log.warn("Intento de crear especialidad con nombre duplicado: {}", request.getNombre());
            throw new TecnicoNoEncontradoException("Ya existe una especialidad con el nombre: " + request.getNombre());
        }

        Especialidad especialidad = especialidadMapper.toEntity(request);
        Especialidad guardada = especialidadRepository.save(especialidad);
        log.info("Especialidad creada exitosamente con id: {}", guardada.getIdEspecialidad());

        return especialidadMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public EspecialidadResponse actualizarEspecialidad(UUID idEspecialidad, ActualizarEspecialidadRequest request) {
        log.info("Actualizando especialidad con id: {}", idEspecialidad);

        Especialidad especialidad = especialidadRepository.findByIdEspecialidad(idEspecialidad)
            .orElseThrow(() -> {
                log.warn("Especialidad no encontrada para actualización: {}", idEspecialidad);
                return EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad);
            });

        if (request.getNombre() != null && !request.getNombre().isBlank()
                && !request.getNombre().equals(especialidad.getNombre())
                && especialidadRepository.existsByNombre(request.getNombre())) {
            log.warn("Intento de actualizar con nombre duplicado: {}", request.getNombre());
            throw new TecnicoNoEncontradoException("Ya existe una especialidad con el nombre: " + request.getNombre());
        }

        especialidadMapper.updateEntityFromDto(request, especialidad);
        Especialidad actualizada = especialidadRepository.save(especialidad);
        log.info("Especialidad actualizada exitosamente con id: {}", actualizada.getIdEspecialidad());

        return especialidadMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminarEspecialidad(UUID idEspecialidad) {
        log.info("Eliminando especialidad con id: {}", idEspecialidad);

        Especialidad especialidad = especialidadRepository.findByIdEspecialidad(idEspecialidad)
            .orElseThrow(() -> {
                log.warn("Especialidad no encontrada para eliminación: {}", idEspecialidad);
                return EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad);
            });

        tecnicoEspecialidadRepository.deleteByIdEspecialidad(idEspecialidad);
        especialidadRepository.delete(especialidad);
        log.info("Especialidad eliminada exitosamente con id: {}", idEspecialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listarEspecialidades() {
        log.debug("Listando todas las especialidades");

        List<Especialidad> especialidades = especialidadRepository.findAll();

        return especialidadMapper.toResponseList(especialidades);
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponse obtenerEspecialidadPorId(UUID idEspecialidad) {
        log.debug("Obteniendo especialidad por id: {}", idEspecialidad);

        Especialidad especialidad = especialidadRepository.findByIdEspecialidad(idEspecialidad)
            .orElseThrow(() -> {
                log.warn("Especialidad no encontrada: {}", idEspecialidad);
                return EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad);
            });

        return especialidadMapper.toResponse(especialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponse obtenerEspecialidadPorNombre(String nombre) {
        log.debug("Obteniendo especialidad por nombre: {}", nombre);

        Especialidad especialidad = especialidadRepository.findByNombre(nombre)
            .orElseThrow(() -> {
                log.warn("Especialidad no encontrada con nombre: {}", nombre);
                return EspecialidadNoEncontradaException.byNombre(nombre);
            });

        return especialidadMapper.toResponse(especialidad);
    }

    @Override
    @Transactional
    public void asignarEspecialidadATecnico(AsignarEspecialidadRequest request) {
        log.info("Asignando especialidad {} a técnico {}", request.getIdEspecialidad(), request.getIdUsuarioTecnico());

        if (!perfilTecnicoRepository.existsByIdUsuario(request.getIdUsuarioTecnico())) {
            log.warn("Técnico no encontrado al asignar especialidad: {}", request.getIdUsuarioTecnico());
            throw TecnicoNoEncontradoException.byIdUsuario(request.getIdUsuarioTecnico());
        }

        if (!especialidadRepository.existsById(request.getIdEspecialidad())) {
            log.warn("Especialidad no encontrada al asignar: {}", request.getIdEspecialidad());
            throw EspecialidadNoEncontradaException.byIdEspecialidad(request.getIdEspecialidad());
        }

        if (tecnicoEspecialidadRepository.existsByIdUsuarioTecnicoAndIdEspecialidad(
            request.getIdUsuarioTecnico(), request.getIdEspecialidad())) {
            log.warn("La especialidad ya está asignada al técnico");
            throw new TecnicoNoEncontradoException(
                "La especialidad ya está asignada al técnico con idUsuario: " + request.getIdUsuarioTecnico()
            );
        }

        TecnicoEspecialidad tecnicoEspecialidad = TecnicoEspecialidad.builder()
            .idUsuarioTecnico(request.getIdUsuarioTecnico())
            .idEspecialidad(request.getIdEspecialidad())
            .certificadoUrl(request.getCertificadoUrl())
            .build();

        tecnicoEspecialidadRepository.save(tecnicoEspecialidad);
        log.info("Especialidad asignada exitosamente al técnico");
    }

    @Override
    @Transactional
    public void removerEspecialidadDeTecnico(UUID idUsuarioTecnico, UUID idEspecialidad) {
        log.info("Removiendo especialidad {} del técnico {}", idEspecialidad, idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al remover especialidad: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        if (!especialidadRepository.existsById(idEspecialidad)) {
            log.warn("Especialidad no encontrada al remover: {}", idEspecialidad);
            throw EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad);
        }

        tecnicoEspecialidadRepository.deleteByIdUsuarioTecnicoAndIdEspecialidad(idUsuarioTecnico, idEspecialidad);
        log.info("Especialidad removida exitosamente del técnico");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadTecnicoResponse> listarEspecialidadesPorTecnico(UUID idUsuarioTecnico) {
        log.debug("Listando especialidades para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al listar especialidades: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        // Cada asignación trae su certificado; lo combinamos con el catálogo
        // (nombre/descripción) de la especialidad.
        return tecnicoEspecialidadRepository.findByIdUsuarioTecnico(idUsuarioTecnico).stream()
            .map(te -> {
                Especialidad esp = especialidadRepository.findByIdEspecialidad(te.getIdEspecialidad())
                    .orElseThrow(() -> EspecialidadNoEncontradaException.byIdEspecialidad(te.getIdEspecialidad()));
                return EspecialidadTecnicoResponse.builder()
                    .idEspecialidad(esp.getIdEspecialidad())
                    .nombre(esp.getNombre())
                    .descripcion(esp.getDescripcion())
                    .certificadoUrl(te.getCertificadoUrl())
                    .build();
            })
            .toList();
    }

    @Override
    @Transactional
    public void actualizarCertificadoEspecialidad(UUID idUsuarioTecnico, UUID idEspecialidad, String certificadoUrl) {
        log.info("Actualizando certificado de especialidad {} para técnico {}", idEspecialidad, idUsuarioTecnico);

        TecnicoEspecialidad asignacion = tecnicoEspecialidadRepository
            .findByIdUsuarioTecnicoAndIdEspecialidad(idUsuarioTecnico, idEspecialidad)
            .orElseThrow(() -> EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad));

        asignacion.setCertificadoUrl(certificadoUrl);
        tecnicoEspecialidadRepository.save(asignacion);
        log.info("Certificado de especialidad actualizado para técnico {}", idUsuarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> listarTecnicosPorEspecialidad(UUID idEspecialidad) {
        log.debug("Listando técnicos por especialidad: {}", idEspecialidad);

        if (!especialidadRepository.existsById(idEspecialidad)) {
            log.warn("Especialidad no encontrada: {}", idEspecialidad);
            throw EspecialidadNoEncontradaException.byIdEspecialidad(idEspecialidad);
        }

        return tecnicoEspecialidadRepository.findTecnicoIdsByEspecialidad(idEspecialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEspecialidad(UUID idEspecialidad) {
        log.debug("Verificando existencia de especialidad por id: {}", idEspecialidad);
        return especialidadRepository.existsById(idEspecialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEspecialidadPorNombre(String nombre) {
        log.debug("Verificando existencia de especialidad por nombre: {}", nombre);
        return especialidadRepository.existsByNombre(nombre);
    }
}
