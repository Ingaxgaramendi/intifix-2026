package com.intifix.modules.apelaciones.dto;

import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RevisarApelacionRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoApelacion estado;

    private String notaAdmin;
}
