package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditarMensajeRequest {

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 4000, message = "El contenido no puede exceder 4000 caracteres")
    private String contenido;
}
