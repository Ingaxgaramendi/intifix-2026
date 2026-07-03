package com.intifix.modules.auth.dto;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {
    private UUID idUsuario;
    private String correo;
    private String telefono;
    private EstadoUsuario estado;
    private Boolean verificado;
    private LocalDateTime ultimoLogin;
    private Set<RolUsuario> roles;
}
