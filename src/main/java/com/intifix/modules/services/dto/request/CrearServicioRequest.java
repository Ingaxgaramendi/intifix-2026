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
import java.util.UUID;

/**
 * Request DTO for creating a new service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearServicioRequest {

    // idCliente removed - obtained from SecurityContextHolder to prevent IDOR

    @NotNull(message = "La ubicación es obligatoria")
    private UUID idUbicacion;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 255, message = "El título debe tener entre 5 y 255 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;

    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadServicio modalidad;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadServicio prioridad;

    @DecimalMin(value = "0.01", message = "El presupuesto máximo debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El presupuesto máximo no puede exceder 999999.99")
    private BigDecimal presupuestoMaximo;

    @NotNull(message = "La fecha programada es obligatoria")
    @Future(message = "La fecha programada debe ser futura")
    private ZonedDateTime fechaProgramada;
}
