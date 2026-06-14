package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.CrearReporteRequest;
import com.intifix.modules.services.dto.response.ReporteResponse;
import com.intifix.modules.services.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Reporte operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
public interface ReporteService {

    ReporteResponse crearReporte(CrearReporteRequest request);

    ReporteResponse actualizarReporte(UUID idReporte, String resolucion, String accionTomada);

    void eliminarReporte(UUID idReporte);

    ReporteResponse obtenerReportePorId(UUID idReporte);

    Page<ReporteResponse> obtenerReportesPorServicio(UUID idServicio, Pageable pageable);

    Page<ReporteResponse> obtenerReportesPorReportante(UUID idReportante, Pageable pageable);

    Page<ReporteResponse> obtenerReportesPorReportado(UUID idReportado, Pageable pageable);

    Page<ReporteResponse> obtenerReportesPorEstado(EstadoReporte estado, Pageable pageable);

    Page<ReporteResponse> obtenerReportesPorTipo(String tipoReporte, Pageable pageable);

    Page<ReporteResponse> obtenerReportesPendientes(Pageable pageable);

    Page<ReporteResponse> obtenerReportesPendientesAltaPrioridad(Pageable pageable);

    long contarReportesPorEstado(EstadoReporte estado);

    long contarReportesPorReportante(UUID idReportante);

    long contarReportesPorReportado(UUID idReportado);

    boolean existeReporte(UUID idReporte);
}
