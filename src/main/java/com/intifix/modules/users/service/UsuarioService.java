package com.intifix.modules.users.service;

import com.intifix.modules.users.entity.Usuario;

import java.math.BigDecimal;
import java.util.UUID;

public interface UsuarioService {
    Usuario registrarUsuario(
        Usuario usuario,
        String nombresCompletos,
        String dniRuc,
        String dniFrontalUrl,
        String dniTraseroUrl,
        String antecedentePenalUrl,
        String certificadoTecnicoUrl,
        Integer experienciaAnios,
        BigDecimal tarifaBase
    );
    Usuario obtenerPorId(UUID id);
    Usuario obtenerPorCorreo(String correo);
}
