package com.intifix.modules.services.dto.request;

import com.intifix.modules.services.enums.ModalidadServicio;
import com.intifix.modules.services.enums.PrioridadServicio;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Request DTO for updating an existing service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarServicioRequest {

    @Size(min = 5, max = 255, message = "El título debe tener entre 5 y 255 caracteres")
    private String titulo;

    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;

    private ModalidadServicio modalidad;

    private PrioridadServicio prioridad;

    @DecimalMin(value = "0.01", message = "El presupuesto máximo debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El presupuesto máximo no puede exceder 999999.99")
    private BigDecimal presupuestoMaximo;

    @Future(message = "La fecha programada debe ser futura")
    private ZonedDateTime fechaProgramada;
}
