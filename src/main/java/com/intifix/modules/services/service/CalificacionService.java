package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.CrearCalificacionRequest;
import com.intifix.modules.services.dto.response.CalificacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Calificacion operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
public interface CalificacionService {

    CalificacionResponse crearCalificacion(CrearCalificacionRequest request);

    void eliminarCalificacion(UUID idCalificacion);

    CalificacionResponse obtenerCalificacionPorId(UUID idCalificacion);

    CalificacionResponse obtenerCalificacionPorServicio(UUID idServicio);

    Page<CalificacionResponse> obtenerCalificacionesPorTecnico(UUID idUsuarioTecnico, Pageable pageable);

    Page<CalificacionResponse> obtenerCalificacionesPorCliente(UUID idCliente, Pageable pageable);

    Double obtenerPromedioPuntuacionTecnico(UUID idUsuarioTecnico);

    Double obtenerPromedioPuntualidadTecnico(UUID idUsuarioTecnico);

    Double obtenerPromedioProfesionalismoTecnico(UUID idUsuarioTecnico);

    Double obtenerPromedioCalidadTrabajoTecnico(UUID idUsuarioTecnico);

    Double obtenerPromedioComunicacionTecnico(UUID idUsuarioTecnico);

    Double obtenerPorcentajeRecomendacionTecnico(UUID idUsuarioTecnico);

    long contarCalificacionesPorTecnico(UUID idUsuarioTecnico);

    long contarCalificacionesPorCliente(UUID idCliente);

    boolean existeCalificacion(UUID idCalificacion);

    boolean existeCalificacionPorServicio(UUID idServicio);
}
