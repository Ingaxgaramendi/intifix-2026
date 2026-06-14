package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.request.ActualizarHorarioRequest;
import com.intifix.modules.technicians.dto.request.CrearHorarioRequest;
import com.intifix.modules.technicians.dto.response.HorarioResponse;

import java.util.List;
import java.util.UUID;

public interface HorarioTecnicoService {

    HorarioResponse crearHorario(CrearHorarioRequest request);

    HorarioResponse actualizarHorario(UUID idHorario, ActualizarHorarioRequest request);

    void eliminarHorario(UUID idHorario);

    List<HorarioResponse> listarHorariosPorTecnico(UUID idUsuarioTecnico);

    HorarioResponse obtenerHorarioPorId(UUID idHorario);

    void validarHorarioSolapado(UUID idUsuarioTecnico, Integer diaSemana, String horaInicio, String horaFin);

    void eliminarHorariosPorTecnico(UUID idUsuarioTecnico);
}
