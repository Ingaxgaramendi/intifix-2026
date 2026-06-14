package com.intifix.modules.services.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a new quotation.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCotizacionRequest {

    @NotNull(message = "El idServicio es obligatorio")
    private UUID idServicio;

    // idUsuarioTecnico removed - obtained from SecurityContextHolder to prevent IDOR

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999999.99")
    private BigDecimal precio;

    @NotBlank(message = "El tiempo estimado es obligatorio")
    @Size(max = 100, message = "El tiempo estimado no puede exceder 100 caracteres")
    private String tiempoEstimado;

    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comentario;
}
