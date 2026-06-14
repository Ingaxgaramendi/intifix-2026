package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.EstadoReporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for report information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {

    private UUID idReporte;
    private UUID idServicio;
    private UUID idReportante;
    private UUID idReportado;
    private String tipoReporte;
    private String motivo;
    private String descripcionDetallada;
    private EstadoReporte estado;
    private String prioridad;
    private String resolucion;
    private String accionTomada;
    private UUID resueltoPor;
    private ZonedDateTime fechaResolucion;
    private ZonedDateTime fechaReporte;
    private ZonedDateTime fechaActualizacion;
    private String[] evidenciasUrl;
    private String metadatos;
}
