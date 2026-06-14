package com.intifix.modules.services.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a report.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearReporteRequest {

    private UUID idServicio;

    // idReportante removed - obtained from SecurityContextHolder to prevent IDOR

    private UUID idReportado;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Size(max = 50, message = "El tipo de reporte no puede exceder 50 caracteres")
    private String tipoReporte;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String motivo;

    @Size(max = 2000, message = "La descripción detallada no puede exceder 2000 caracteres")
    private String descripcionDetallada;

    @Size(max = 20, message = "La prioridad no puede exceder 20 caracteres")
    private String prioridad;

    private String[] evidenciasUrl;
}
