package com.intifix.modules.services.dto.request;

import com.intifix.modules.services.enums.EstadoCotizacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for responding to a quotation (accept/reject).
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderCotizacionRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoCotizacion estado;

    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;
}
