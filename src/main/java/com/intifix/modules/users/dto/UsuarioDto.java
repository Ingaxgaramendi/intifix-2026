package com.intifix.modules.users.dto;

import com.intifix.modules.users.entity.EstadoUsuario;
import com.intifix.modules.users.entity.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private UUID id;
    private String correo;
    private String telefono;
    private EstadoUsuario estado;
    private Set<RolUsuario> roles;
}
