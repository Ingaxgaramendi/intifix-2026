package com.intifix.modules.technicians.service.impl;

import com.intifix.modules.technicians.dto.request.ActualizarHorarioRequest;
import com.intifix.modules.technicians.dto.request.CrearHorarioRequest;
import com.intifix.modules.technicians.dto.response.HorarioResponse;
import com.intifix.modules.technicians.entity.HorarioTecnico;
import com.intifix.modules.technicians.exception.HorarioDuplicadoException;
import com.intifix.modules.technicians.exception.TecnicoNoEncontradoException;
import com.intifix.modules.technicians.mapper.HorarioMapper;
import com.intifix.modules.technicians.repository.HorarioTecnicoRepository;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import com.intifix.modules.technicians.service.HorarioTecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioTecnicoServiceImpl implements HorarioTecnicoService {

    private final HorarioTecnicoRepository horarioTecnicoRepository;
    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final HorarioMapper horarioMapper;

    @Override
    @Transactional
    public HorarioResponse crearHorario(CrearHorarioRequest request) {
        log.info("Creando horario para técnico idUsuario: {}", request.getIdUsuarioTecnico());

        if (!perfilTecnicoRepository.existsByIdUsuario(request.getIdUsuarioTecnico())) {
            log.warn("Técnico no encontrado al crear horario: {}", request.getIdUsuarioTecnico());
            throw TecnicoNoEncontradoException.byIdUsuario(request.getIdUsuarioTecnico());
        }

        validarHorarioSolapado(
            request.getIdUsuarioTecnico(),
            request.getDiaSemana(),
            request.getHoraInicio().toString(),
            request.getHoraFin().toString()
        );

        HorarioTecnico horarioTecnico = horarioMapper.toEntity(request);
        HorarioTecnico guardado = horarioTecnicoRepository.save(horarioTecnico);
        log.info("Horario creado exitosamente con id: {}", guardado.getIdHorario());

        return horarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public HorarioResponse actualizarHorario(UUID idHorario, ActualizarHorarioRequest request) {
        log.info("Actualizando horario con id: {}", idHorario);

        HorarioTecnico horarioTecnico = horarioTecnicoRepository.findByIdHorario(idHorario)
            .orElseThrow(() -> {
                log.warn("Horario no encontrado para actualización: {}", idHorario);
                return new TecnicoNoEncontradoException("Horario no encontrado con id: " + idHorario);
            });

        if (request.getDiaSemana() != null && request.getHoraInicio() != null && request.getHoraFin() != null) {
            validarHorarioSolapado(
                horarioTecnico.getIdUsuarioTecnico(),
                request.getDiaSemana(),
                request.getHoraInicio().toString(),
                request.getHoraFin().toString(),
                idHorario
            );
        }

        horarioMapper.updateEntityFromDto(request, horarioTecnico);
        HorarioTecnico actualizado = horarioTecnicoRepository.save(horarioTecnico);
        log.info("Horario actualizado exitosamente con id: {}", actualizado.getIdHorario());

        return horarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminarHorario(UUID idHorario) {
        log.info("Eliminando horario con id: {}", idHorario);

        HorarioTecnico horarioTecnico = horarioTecnicoRepository.findByIdHorario(idHorario)
            .orElseThrow(() -> {
                log.warn("Horario no encontrado para eliminación: {}", idHorario);
                return new TecnicoNoEncontradoException("Horario no encontrado con id: " + idHorario);
            });

        horarioTecnicoRepository.delete(horarioTecnico);
        log.info("Horario eliminado exitosamente con id: {}", idHorario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> listarHorariosPorTecnico(UUID idUsuarioTecnico) {
        log.debug("Listando horarios para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al listar horarios: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        List<HorarioTecnico> horarios = horarioTecnicoRepository.findHorariosActivosByTecnico(idUsuarioTecnico);

        return horarioMapper.toResponseList(horarios);
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponse obtenerHorarioPorId(UUID idHorario) {
        log.debug("Obteniendo horario por id: {}", idHorario);

        HorarioTecnico horarioTecnico = horarioTecnicoRepository.findByIdHorario(idHorario)
            .orElseThrow(() -> {
                log.warn("Horario no encontrado: {}", idHorario);
                return new TecnicoNoEncontradoException("Horario no encontrado con id: " + idHorario);
            });

        return horarioMapper.toResponse(horarioTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarHorarioSolapado(UUID idUsuarioTecnico, Integer diaSemana, String horaInicio, String horaFin) {
        validarHorarioSolapado(idUsuarioTecnico, diaSemana, horaInicio, horaFin, null);
    }

    private void validarHorarioSolapado(UUID idUsuarioTecnico, Integer diaSemana, String horaInicio, String horaFin, UUID idHorarioExcluir) {
        log.debug("Validando solapamiento de horario para técnico: {}, día: {}", idUsuarioTecnico, diaSemana);

        LocalTime inicio = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);

        List<HorarioTecnico> horariosSolapados = horarioTecnicoRepository.findHorariosSolapados(
            idUsuarioTecnico, diaSemana, inicio, fin
        );

        if (idHorarioExcluir != null) {
            horariosSolapados.removeIf(h -> h.getIdHorario().equals(idHorarioExcluir));
        }

        if (!horariosSolapados.isEmpty()) {
            log.warn("Se detectó solapamiento de horario para técnico: {}, día: {}", idUsuarioTecnico, diaSemana);
            throw HorarioDuplicadoException.porSolapamiento(diaSemana, horaInicio, horaFin);
        }
    }

    @Override
    @Transactional
    public void eliminarHorariosPorTecnico(UUID idUsuarioTecnico) {
        log.info("Eliminando todos los horarios para técnico idUsuario: {}", idUsuarioTecnico);

        if (!perfilTecnicoRepository.existsByIdUsuario(idUsuarioTecnico)) {
            log.warn("Técnico no encontrado al eliminar horarios: {}", idUsuarioTecnico);
            throw TecnicoNoEncontradoException.byIdUsuario(idUsuarioTecnico);
        }

        horarioTecnicoRepository.deleteByIdUsuarioTecnico(idUsuarioTecnico);
        log.info("Horarios eliminados exitosamente para técnico idUsuario: {}", idUsuarioTecnico);
    }
}
