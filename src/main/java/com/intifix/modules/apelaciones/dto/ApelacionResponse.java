package com.intifix.modules.apelaciones.dto;

import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import com.intifix.modules.apelaciones.entity.TipoApelacion;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data @Builder
public class ApelacionResponse {
    private UUID idApelacion;
    private String correo;
    private TipoApelacion tipo;
    private String mensaje;
    private EstadoApelacion estado;
    private String notaAdmin;
    private ZonedDateTime fechaEnvio;
}
