package com.intifix.modules.users.controller;

import com.intifix.modules.users.dto.UsuarioDto;
import com.intifix.modules.users.entity.RolUsuario;
import com.intifix.modules.users.entity.Usuario;
import com.intifix.modules.users.service.UsuarioService;
import com.intifix.shared.api.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioDto>> registrar(@RequestBody RegistroRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
            .correo(request.correo)
            .clave(request.clave)
            .telefono(request.telefono)
            .roles(request.roles)
            .build();

        Usuario registrado = usuarioService.registrarUsuario(
            nuevoUsuario,
            request.nombresCompletos,
            request.dniRuc,
            request.dniFrontalUrl,
            request.dniTraseroUrl,
            request.antecedentePenalUrl,
            request.certificadoTecnicoUrl,
            request.experienciaAnios,
            request.tarifaBase
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Usuario registrado con exito en INTIFIX.", mapearToDto(registrado)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDto>> obtenerPerfil(@PathVariable UUID id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success("Datos obtenidos.", mapearToDto(usuario)));
    }

    private UsuarioDto mapearToDto(Usuario usuario) {
        return UsuarioDto.builder()
            .id(usuario.getId())
            .correo(usuario.getCorreo())
            .telefono(usuario.getTelefono())
            .estado(usuario.getEstado())
            .roles(usuario.getRoles())
            .build();
    }

    @Data
    public static class RegistroRequest {
        private String correo;
        private String clave;
        private String nombresCompletos;
        private String dniRuc;
        private String telefono;
        private Set<RolUsuario> roles;
        private String dniFrontalUrl;
        private String dniTraseroUrl;
        private String antecedentePenalUrl;
        private String certificadoTecnicoUrl;
        private Integer experienciaAnios;
        private BigDecimal tarifaBase;

        public String getNombre() {
            return nombresCompletos;
        }

        public void setNombre(String nombre) {
            this.nombresCompletos = nombre;
        }

        public String getDni() {
            return dniRuc;
        }

        public void setDni(String dni) {
            this.dniRuc = dni;
        }

        public String getRuc() {
            return dniRuc;
        }

        public void setRuc(String ruc) {
            this.dniRuc = ruc;
        }
    }
}
