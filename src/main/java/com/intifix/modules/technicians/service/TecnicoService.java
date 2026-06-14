package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.request.ActualizarTecnicoRequest;
import com.intifix.modules.technicians.dto.request.CambiarDisponibilidadRequest;
import com.intifix.modules.technicians.dto.request.CrearTecnicoRequest;
import com.intifix.modules.technicians.dto.response.TecnicoDetalleResponse;
import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TecnicoService {

    TecnicoResponse crearTecnico(CrearTecnicoRequest request);

    TecnicoResponse obtenerTecnicoPorId(UUID idUsuario);

    TecnicoDetalleResponse obtenerDetalleTecnicoPorId(UUID idUsuario);

    TecnicoResponse actualizarTecnico(UUID idUsuario, ActualizarTecnicoRequest request);

    void eliminarTecnico(UUID idUsuario);

    Page<TecnicoResponse> obtenerTodosTecnicos(Pageable pageable);

    TecnicoResponse buscarTecnicoPorDniRuc(String dniRuc);

    Page<TecnicoResponse> buscarTecnicosPorDisponibilidad(DisponibilidadTecnico disponibilidad, Pageable pageable);

    Page<TecnicoResponse> buscarTecnicosPorEspecialidad(UUID idEspecialidad, Pageable pageable);

    TecnicoResponse aprobarTecnico(UUID idUsuario);

    TecnicoResponse rechazarTecnico(UUID idUsuario);

    TecnicoResponse cambiarDisponibilidad(UUID idUsuario, CambiarDisponibilidadRequest request);

    TecnicoResponse actualizarDocumentos(UUID idUsuario, ActualizarTecnicoRequest request);

    Page<TecnicoResponse> buscarTecnicosPorEstado(EstadoAprobacionTecnico estado, Pageable pageable);

    boolean existeTecnico(UUID idUsuario);

    boolean existeTecnicoPorDniRuc(String dniRuc);

    long contarTotalTecnicos();

    long contarTecnicosAprobados();

    long contarTecnicosActivos();

    TecnicoResponse asignarUbicacion(UUID idUsuario, UUID idUbicacion);

    TecnicoResponse actualizarUbicacion(UUID idUsuario, UUID idUbicacion);

    java.util.List<TecnicoResponse> obtenerTecnicosPorUbicacion(UUID idUbicacion);

    java.util.List<TecnicoResponse> obtenerTecnicosDisponiblesPorUbicacion(UUID idUbicacion);

    java.util.List<TecnicoResponse> obtenerTecnicosAprobadosPorUbicacion(UUID idUbicacion);

    java.util.List<TecnicoResponse> obtenerTecnicosDisponiblesYAprobadosPorUbicacion(UUID idUbicacion);

    long contarTecnicosPorUbicacion(UUID idUbicacion);

    long contarTecnicosDisponiblesPorUbicacion(UUID idUbicacion);

    long contarTecnicosAprobadosPorUbicacion(UUID idUbicacion);

    long contarTecnicosDisponiblesYAprobadosPorUbicacion(UUID idUbicacion);
}
