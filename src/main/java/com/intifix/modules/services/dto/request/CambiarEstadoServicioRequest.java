package com.intifix.modules.services.dto.request;

import com.intifix.modules.services.enums.EstadoServicio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing the state of a service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoServicioRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoServicio estado;

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;
}
