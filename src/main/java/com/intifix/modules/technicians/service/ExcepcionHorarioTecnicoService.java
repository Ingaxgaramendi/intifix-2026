package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.request.CrearExcepcionHorarioRequest;
import com.intifix.modules.technicians.dto.response.ExcepcionHorarioResponse;

import java.util.List;
import java.util.UUID;

public interface ExcepcionHorarioTecnicoService {

    ExcepcionHorarioResponse crearExcepcion(CrearExcepcionHorarioRequest request);

    void eliminarExcepcion(UUID idExcepcion);

    List<ExcepcionHorarioResponse> listarExcepcionesPorTecnico(UUID idUsuarioTecnico);

    ExcepcionHorarioResponse obtenerExcepcionPorId(UUID idExcepcion);

    void eliminarExcepcionesPorTecnico(UUID idUsuarioTecnico);
}
