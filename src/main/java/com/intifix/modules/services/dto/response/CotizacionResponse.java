package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.EstadoCotizacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for quotation information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionResponse {

    private UUID idCotizacion;
    private UUID idServicio;
    private UUID idUsuarioTecnico;
    private BigDecimal precio;
    private String tiempoEstimado;
    private EstadoCotizacion estado;
    private String comentario;
    private ZonedDateTime fechaEnvio;
    private ZonedDateTime fechaRespuesta;
    private ZonedDateTime fechaExpiracion;
    private String motivoRechazo;
}
