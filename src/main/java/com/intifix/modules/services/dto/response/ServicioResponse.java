package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for basic service information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponse {

    private UUID idServicio;
    private UUID idCliente;
    private UUID idUbicacion;
    private String titulo;
    private String descripcion;
    private ModalidadServicio modalidad;
    private PrioridadServicio prioridad;
    private EstadoServicio estado;
    private BigDecimal presupuestoMaximo;
    private ZonedDateTime fechaProgramada;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime fechaActualizacion;
}
