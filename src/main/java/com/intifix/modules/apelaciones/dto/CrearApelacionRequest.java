package com.intifix.modules.apelaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearApelacionRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo no válido")
    private String correo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 20, max = 2000, message = "El mensaje debe tener entre 20 y 2000 caracteres")
    private String mensaje;
}
