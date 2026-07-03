package com.intifix.modules.services.mapper;

import com.intifix.modules.services.dto.response.CalificacionResponse;
import com.intifix.modules.services.dto.response.CotizacionResponse;
import com.intifix.modules.services.dto.response.EvidenciaServicioResponse;
import com.intifix.modules.services.dto.response.ServicioDetalleResponse;
import com.intifix.modules.services.entity.AsignacionServicio;
import com.intifix.modules.services.entity.Servicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Manual mapper for aggregating ServicioDetalleResponse from multiple entities.
 * 
 * This mapper handles complex aggregation logic that cannot be easily expressed
 * in MapStruct, combining data from Servicio, AsignacionServicio, and related DTOs.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ServicioDetalleMapper {

    private final ServicioMapper servicioMapper;
    private final CotizacionMapper cotizacionMapper;
    private final EvidenciaMapper evidenciaMapper;
    private final CalificacionMapper calificacionMapper;

    /**
     * Maps Servicio entity and related data to ServicioDetalleResponse.
     * 
     * @param servicio The main service entity
     * @param asignacion Optional assignment entity (can be null)
     * @param cotizaciones List of quotation responses
     * @param evidencias List of evidence responses
     * @param calificacion Optional rating response (can be null)
     * @return Aggregated ServicioDetalleResponse
     */
    public ServicioDetalleResponse toDetalleResponse(
            Servicio servicio,
            AsignacionServicio asignacion,
            List<CotizacionResponse> cotizaciones,
            List<EvidenciaServicioResponse> evidencias,
            CalificacionResponse calificacion) {
        
        return ServicioDetalleResponse.builder()
            .idServicio(servicio.getIdServicio())
            .idCliente(servicio.getIdCliente())
            .idUbicacion(servicio.getIdUbicacion())
            .idEspecialidad(servicio.getIdEspecialidad())
            .titulo(servicio.getTitulo())
            .descripcion(servicio.getDescripcion())
            .fotos(servicio.getFotos())
            .modalidad(servicio.getModalidad())
            .estado(servicio.getEstado())
            .presupuestoMaximo(servicio.getPresupuestoMaximo())
            .fechaProgramada(servicio.getFechaProgramada())
            .fechaCreacion(servicio.getFechaCreacion())
            .fechaActualizacion(servicio.getFechaActualizacion())
            .fechaFinalizacion(servicio.getFechaFinalizacion())
            .motivoCancelacion(servicio.getMotivoCancelacion())
            .idAsignacion(asignacion != null ? asignacion.getIdAsignacion() : null)
            .idUsuarioTecnico(asignacion != null ? asignacion.getIdUsuarioTecnico() : null)
            .fechaAsignacion(asignacion != null ? asignacion.getFechaAsignacion() : null)
            .fechaInicioReal(asignacion != null ? asignacion.getFechaInicioReal() : null)
            .fechaFinReal(asignacion != null ? asignacion.getFechaFinReal() : null)
            .cotizaciones(cotizaciones)
            .evidencias(evidencias)
            .calificacion(calificacion)
            .build();
    }
}
