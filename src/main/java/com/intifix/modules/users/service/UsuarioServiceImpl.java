package com.intifix.modules.users.service;

import com.intifix.modules.users.entity.PerfilCliente;
import com.intifix.modules.users.entity.PerfilTecnico;
import com.intifix.modules.users.entity.RolUsuario;
import com.intifix.modules.users.entity.Usuario;
import com.intifix.modules.users.repository.PerfilClienteRepository;
import com.intifix.modules.users.repository.PerfilTecnicoRepository;
import com.intifix.modules.users.repository.UsuarioRepository;
import com.intifix.shared.dto.UserEventDTO;
import com.intifix.shared.events.domain.UsuarioCreadoEvent;
import com.intifix.shared.exception.CustomException;
import com.intifix.shared.exception.ResourceNotFoundException;
import com.intifix.shared.security.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilClienteRepository perfilClienteRepository;
    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Usuario registrarUsuario(
        Usuario usuario,
        String nombresCompletos,
        String dniRuc,
        String dniFrontalUrl,
        String dniTraseroUrl,
        String antecedentePenalUrl,
        String certificadoTecnicoUrl,
        Integer experienciaAnios,
        BigDecimal tarifaBase
    ) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new CustomException("El correo electronico ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            throw new CustomException("El telefono ya se encuentra registrado.");
        }
        if (!StringUtils.hasText(nombresCompletos)) {
            throw new CustomException("Los nombres completos son obligatorios.");
        }

        usuario.setClave(EncryptionUtils.encriptarTexto(usuario.getClave()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        if (usuarioGuardado.getRoles().contains(RolUsuario.CLIENTE)) {
            PerfilCliente cliente = PerfilCliente.builder()
                .id(usuarioGuardado.getId())
                .usuario(usuarioGuardado)
                .nombresCompletos(nombresCompletos)
                .dniRuc(dniRuc)
                .build();
            perfilClienteRepository.save(cliente);
        }

        if (usuarioGuardado.getRoles().contains(RolUsuario.TECNICO)) {
            validarPerfilTecnico(dniRuc, dniFrontalUrl, dniTraseroUrl, antecedentePenalUrl);
            if (perfilTecnicoRepository.existsByDniRuc(dniRuc)) {
                throw new CustomException("El DNI/RUC ingresado ya pertenece a otro tecnico.");
            }

            PerfilTecnico tecnico = PerfilTecnico.builder()
                .id(usuarioGuardado.getId())
                .usuario(usuarioGuardado)
                .nombresCompletos(nombresCompletos)
                .dniRuc(dniRuc)
                .experienciaAnios(experienciaAnios == null ? 0 : experienciaAnios)
                .tarifaBase(tarifaBase == null ? BigDecimal.ZERO : tarifaBase)
                .dniFrontalUrl(dniFrontalUrl)
                .dniTraseroUrl(dniTraseroUrl)
                .antecedentePenalUrl(antecedentePenalUrl)
                .certificadoTecnicoUrl(certificadoTecnicoUrl)
                .build();
            perfilTecnicoRepository.save(tecnico);
        }

        String rolPrincipal = usuarioGuardado.getRoles().isEmpty()
            ? RolUsuario.CLIENTE.name()
            : usuarioGuardado.getRoles().iterator().next().name();

        UserEventDTO dto = new UserEventDTO(usuarioGuardado.getId(), nombresCompletos, usuarioGuardado.getCorreo(), rolPrincipal);
        eventPublisher.publishEvent(new UsuarioCreadoEvent(dto));

        return usuarioGuardado;
    }

    private void validarPerfilTecnico(String dniRuc, String dniFrontalUrl, String dniTraseroUrl, String antecedentePenalUrl) {
        if (!StringUtils.hasText(dniRuc)) {
            throw new CustomException("El DNI/RUC del tecnico es obligatorio.");
        }
        if (!StringUtils.hasText(dniFrontalUrl) || !StringUtils.hasText(dniTraseroUrl) || !StringUtils.hasText(antecedentePenalUrl)) {
            throw new CustomException("El tecnico debe adjuntar DNI frontal, DNI trasero y antecedente penal.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(UUID id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontro ningun usuario con el ID solicitado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con el correo electronico ingresado."));
    }
}
