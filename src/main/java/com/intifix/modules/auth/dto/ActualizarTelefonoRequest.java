package com.intifix.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarTelefonoRequest {

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{9,20}$", message = "El teléfono debe tener entre 9 y 20 dígitos")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
}
