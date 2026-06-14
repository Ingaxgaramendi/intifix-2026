package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.AsignarTecnicoRequest;
import com.intifix.modules.services.dto.response.AsignacionServicioResponse;
import com.intifix.modules.services.enums.EstadoServicio;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for AsignacionServicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
public interface AsignacionServicioService {

    AsignacionServicioResponse asignarTecnico(UUID idServicio, AsignarTecnicoRequest request);

    AsignacionServicioResponse actualizarAsignacion(UUID idAsignacion, AsignarTecnicoRequest request);

    void eliminarAsignacion(UUID idAsignacion);

    AsignacionServicioResponse obtenerAsignacionPorId(UUID idAsignacion);

    AsignacionServicioResponse obtenerAsignacionPorServicio(UUID idServicio);

    List<AsignacionServicioResponse> obtenerAsignacionesPorTecnico(UUID idUsuarioTecnico);

    List<AsignacionServicioResponse> obtenerAsignacionesPorEstado(EstadoServicio estado);

    void iniciarServicio(UUID idAsignacion);

    void finalizarServicio(UUID idAsignacion);

    long contarAsignacionesPorTecnico(UUID idUsuarioTecnico);

    long contarAsignacionesPorEstado(EstadoServicio estado);

    boolean existeAsignacionPorServicio(UUID idServicio);
}
