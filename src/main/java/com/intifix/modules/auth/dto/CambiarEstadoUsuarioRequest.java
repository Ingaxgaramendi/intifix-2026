package com.intifix.modules.auth.dto;

import com.intifix.modules.auth.entity.EstadoUsuario;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoUsuarioRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoUsuario estado;

    private String motivo;
}
