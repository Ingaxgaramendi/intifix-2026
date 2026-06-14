package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.request.ActualizarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.AsignarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.CrearEspecialidadRequest;
import com.intifix.modules.technicians.dto.response.EspecialidadResponse;

import java.util.List;
import java.util.UUID;

public interface EspecialidadService {

    EspecialidadResponse crearEspecialidad(CrearEspecialidadRequest request);

    EspecialidadResponse actualizarEspecialidad(UUID idEspecialidad, ActualizarEspecialidadRequest request);

    void eliminarEspecialidad(UUID idEspecialidad);

    List<EspecialidadResponse> listarEspecialidades();

    EspecialidadResponse obtenerEspecialidadPorId(UUID idEspecialidad);

    EspecialidadResponse obtenerEspecialidadPorNombre(String nombre);

    void asignarEspecialidadATecnico(AsignarEspecialidadRequest request);

    void removerEspecialidadDeTecnico(UUID idUsuarioTecnico, UUID idEspecialidad);

    List<EspecialidadResponse> listarEspecialidadesPorTecnico(UUID idUsuarioTecnico);

    List<UUID> listarTecnicosPorEspecialidad(UUID idEspecialidad);

    boolean existeEspecialidad(UUID idEspecialidad);

    boolean existeEspecialidadPorNombre(String nombre);
}
