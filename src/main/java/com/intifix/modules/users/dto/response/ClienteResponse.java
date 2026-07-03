package com.intifix.modules.users.dto.response;

import com.intifix.modules.auth.entity.EstadoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private UUID idUsuario;
    private String nombresCompletos;
    private String dniRuc;
    private String fotoPerfilUrl;
    private UUID idUbicacion;
    private OffsetDateTime creadoEn;
    private EstadoUsuario estadoUsuario;
}
