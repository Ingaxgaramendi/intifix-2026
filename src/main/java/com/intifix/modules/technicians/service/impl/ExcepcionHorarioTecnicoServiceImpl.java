package com.intifix.modules.technicians.service.impl;

import com.intifix.modules.technicians.dto.request.CrearExcepcionHorarioRequest;
import com.intifix.modules.technicians.dto.response.ExcepcionHorarioResponse;
import com.intifix.modules.technicians.entity.ExcepcionHorarioTecnico;
import com.intifix.modules.technicians.exception.TecnicoNoEncontradoException;
import com.intifix.modules.technicians.mapper.ExcepcionHorarioMapper;
import com.intifix.modules.technicians.repository.ExcepcionHorarioTecnicoRepository;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import com.intifix.modules.technicians.service.ExcepcionHorarioTecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcepcionHorarioTecnicoServiceImpl implements ExcepcionHorarioTecnicoService {

    private final ExcepcionHorarioTecnicoRepository excepcionHorarioTecnicoRepository;
    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final ExcepcionHorarioMapper excepcionHorarioMapper;

    @Override
    @Transactional
    public ExcepcionHorarioResponse crearExcepcion(CrearExcepcionHorarioRequest request) {
        log.info("Creando excepción de horario para técnico idUsuario: {}", request.getIdUsuarioTecnico());

        if (!perfilTecnicoRepository.existsByIdUsuario(request.getIdUsuarioTecnico())) {
            log.warn("Técnico no encontrado al crear excepción: {}", request.getIdUsuarioTecnico());
            throw TecnicoNoEncontradoException.byIdUsuario(request.getIdUsuarioTecnico());
        }

        ExcepcionHorarioTecnico excepcion = excepcionHorarioMapper.toEntity(request);
        excepcion.setIdUsuarioTecnico(request.getIdUsuarioTecnico());

        ExcepcionHorarioTecnico guardado = excepcionHorarioTecnicoRepository.save(excepcion);
        log.info("Excepción de horario creada exitosamente con id: {}", guardado.getIdExcepcion());

        return excepcionHorarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminarExcepcion(UUID idExcepcion) {
        log.info("Eliminando excepción de horario con id: {}", idExcepcion);

        ExcepcionHorarioTecnico excepcion = excepcionHorarioTecnicoRepository.findByIdExcepcion(idExcepcion)
            .orElseThrow(() -> {
                log.warn("Excepción de horario no encontrada para eliminación: {}", idExcepcion);
                return new TecnicoNoEncontradoException("Excepción de horario no encontrada con id: " + idExcepcion);
            });

        excepcionHorarioTecnicoRepository.delete(excepcion);
        log.info("Excepción de horario eliminada exitosamente con id: {}", idExcepcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExcepcionHorarioResponse> listarExcepcionesPorTecnico(UUID idUsuarioTecnico) {
        log.debug("Listando excepciones de horario para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al listar excepciones: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        List<ExcepcionHorarioTecnico> excepciones = excepcionHorarioTecnicoRepository.findByIdUsuarioTecnico(idUsuarioTecnico);

        return excepcionHorarioMapper.toResponseList(excepciones);
    }

    @Override
    @Transactional(readOnly = true)
    public ExcepcionHorarioResponse obtenerExcepcionPorId(UUID idExcepcion) {
        log.debug("Obteniendo excepción de horario por id: {}", idExcepcion);

        ExcepcionHorarioTecnico excepcion = excepcionHorarioTecnicoRepository.findByIdExcepcion(idExcepcion)
            .orElseThrow(() -> {
                log.warn("Excepción de horario no encontrada: {}", idExcepcion);
                return new TecnicoNoEncontradoException("Excepción de horario no encontrada con id: " + idExcepcion);
            });

        return excepcionHorarioMapper.toResponse(excepcion);
    }

    @Override
    @Transactional
    public void eliminarExcepcionesPorTecnico(UUID idUsuarioTecnico) {
        log.info("Eliminando todas las excepciones de horario para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al eliminar excepciones: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        excepcionHorarioTecnicoRepository.deleteByIdUsuarioTecnico(idUsuarioTecnico);
        log.info("Excepciones de horario eliminadas exitosamente para técnico idUsuario: {}", idUsuarioTecnico);
    }
}
