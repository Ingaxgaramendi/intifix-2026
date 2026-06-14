package com.intifix.modules.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearClienteRequest {

    @NotNull(message = "El idUsuario es obligatorio")
    private UUID idUsuario;

    @NotBlank(message = "Los nombres completos son obligatorios")
    @Size(min = 2, max = 255, message = "Los nombres completos deben tener entre 2 y 255 caracteres")
    @Pattern(
        regexp = "^[\\p{L}\\p{M}.'\\- ]+$",
        message = "Los nombres completos solo pueden contener letras, espacios, puntos, apóstrofes y guiones"
    )
    private String nombresCompletos;

    @Pattern(
        regexp = "^(\\d{8}|\\d{11})$",
        message = "El documento debe ser un DNI de 8 dígitos o un RUC de 11 dígitos"
    )
    private String dniRuc;

    @URL(regexp = "^https?://.*", message = "La foto de perfil debe ser una URL http(s) válida")
    @Size(max = 2048, message = "La URL de la foto de perfil no puede exceder 2048 caracteres")
    private String fotoPerfilUrl;
}
