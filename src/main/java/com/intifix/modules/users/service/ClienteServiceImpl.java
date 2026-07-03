package com.intifix.modules.users.service;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import com.intifix.modules.users.dto.request.ActualizarClienteRequest;
import com.intifix.modules.users.dto.request.CrearClienteRequest;
import com.intifix.modules.users.dto.response.ClienteDetalleResponse;
import com.intifix.modules.users.dto.response.ClientePerfilPublicoResponse;
import com.intifix.modules.users.dto.response.ClienteResponse;
import com.intifix.modules.users.entity.PerfilCliente;
import com.intifix.modules.geo.dto.response.UbicacionPublicaResponse;
import com.intifix.modules.geo.service.UbicacionService;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.modules.users.exception.ClienteNoEncontradoException;
import com.intifix.modules.users.exception.ClienteYaExisteException;
import com.intifix.modules.users.exception.DniDuplicadoException;
import com.intifix.modules.users.exception.UsuarioNoExisteException;
import com.intifix.modules.users.gateway.UserGateway;
import com.intifix.modules.users.mapper.ClienteMapper;
import com.intifix.modules.users.repository.PerfilClienteRepository;
import com.intifix.modules.audit.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final PerfilClienteRepository perfilClienteRepository;
    private final ClienteMapper clienteMapper;
    private final UserGateway userGateway;
    private final ApplicationEventPublisher eventPublisher;
    private final UbicacionService ubicacionService;
    private final ServicioRepository servicioRepository;
    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    @Transactional
    public ClienteResponse crearCliente(CrearClienteRequest request) {
        UUID idUsuario = request.getIdUsuario();
        log.info("Creando perfil de cliente para idUsuario: {}", idUsuario);

        if (!userGateway.existeUsuario(idUsuario)) {
            log.warn("Intento de crear perfil para usuario inexistente: {}", idUsuario);
            throw UsuarioNoExisteException.byId(idUsuario);
        }

        if (perfilClienteRepository.existsById(idUsuario)) {
            log.warn("Intento de crear perfil duplicado para idUsuario: {}", idUsuario);
            throw ClienteYaExisteException.byIdUsuario(idUsuario);
        }

        validarDniRucDisponible(request.getDniRuc());

        PerfilCliente guardado = perfilClienteRepository.save(clienteMapper.toEntity(request));
        log.info("Perfil de cliente creado para idUsuario: {}", guardado.getIdUsuario());

        return clienteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(UUID idUsuario) {
        ClienteResponse response = clienteMapper.toResponse(obtenerPerfil(idUsuario));
        usuarioAuthRepository.obtenerEstadoPorId(idUsuario).ifPresent(response::setEstadoUsuario);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDetalleResponse obtenerDetalleClientePorId(UUID idUsuario) {
        return clienteMapper.toDetalleResponse(obtenerPerfil(idUsuario));
    }

    @Override
    @Transactional
    public ClienteResponse actualizarCliente(UUID idUsuario, ActualizarClienteRequest request) {
        log.info("Actualizando perfil de cliente para idUsuario: {}", idUsuario);

        PerfilCliente perfilCliente = obtenerPerfil(idUsuario);

        String nuevoDniRuc = request.getDniRuc();
        if (nuevoDniRuc != null
                && !nuevoDniRuc.equals(perfilCliente.getDniRuc())
                && perfilClienteRepository.existsByDniRucAndIdUsuarioNot(nuevoDniRuc, idUsuario)) {
            log.warn("Intento de actualizar con DNI/RUC duplicado: {}", maskDocumento(nuevoDniRuc));
            throw DniDuplicadoException.byDniRuc(maskDocumento(nuevoDniRuc));
        }

        // Snapshot previo para el diff de auditoría (antes de mutar la entidad).
        ClienteResponse anterior = clienteMapper.toResponse(perfilCliente);

        clienteMapper.updateEntityFromDto(request, perfilCliente);

        PerfilCliente actualizado = perfilClienteRepository.save(perfilCliente);
        log.info("Perfil de cliente actualizado para idUsuario: {}", actualizado.getIdUsuario());

        ClienteResponse nuevo = clienteMapper.toResponse(actualizado);
        eventPublisher.publishEvent(new UserUpdatedEvent(idUsuario, anterior, nuevo));

        return nuevo;
    }

    @Override
    @Transactional
    public ClienteResponse asignarUbicacion(UUID idUsuario, UUID idUbicacion) {
        log.info("Asignando ubicación {} al cliente {}", idUbicacion, idUsuario);

        PerfilCliente perfilCliente = obtenerPerfil(idUsuario);
        // Valida que la ubicación exista (lanza 404 si no); reusa el módulo geo.
        ubicacionService.obtenerPorId(idUbicacion);

        perfilCliente.setIdUbicacion(idUbicacion);
        PerfilCliente actualizado = perfilClienteRepository.save(perfilCliente);
        log.info("Ubicación asignada al cliente {}: {}", idUsuario, idUbicacion);

        return clienteMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePerfilPublicoResponse obtenerPerfilPublico(UUID idUsuario) {
        PerfilCliente perfil = obtenerPerfil(idUsuario);

        ClientePerfilPublicoResponse.ClientePerfilPublicoResponseBuilder builder =
            ClientePerfilPublicoResponse.builder()
                .idUsuario(perfil.getIdUsuario())
                .nombresCompletos(perfil.getNombresCompletos())
                .fotoPerfilUrl(perfil.getFotoPerfilUrl())
                .creadoEn(perfil.getCreadoEn())
                .totalServicios(servicioRepository.countByIdCliente(idUsuario))
                .tieneUbicacion(perfil.getIdUbicacion() != null);

        // Solo distrito/zona y coords aproximadas: nunca la dirección exacta.
        if (perfil.getIdUbicacion() != null) {
            UbicacionPublicaResponse ubic = ubicacionService.obtenerPorId(perfil.getIdUbicacion());
            builder.distrito(ubic.getDistrito())
                   .provincia(ubic.getProvincia())
                   .latitud(ubic.getLatitud())
                   .longitud(ubic.getLongitud());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void eliminarCliente(UUID idUsuario) {
        log.info("Eliminando perfil de cliente para idUsuario: {}", idUsuario);

        PerfilCliente perfilCliente = obtenerPerfil(idUsuario);
        perfilClienteRepository.delete(perfilCliente);

        log.info("Perfil de cliente eliminado para idUsuario: {}", idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> obtenerTodosClientes(Pageable pageable) {
        return enriquecerConEstado(perfilClienteRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> buscarClientesPorNombre(String nombre, Pageable pageable) {
        return enriquecerConEstado(perfilClienteRepository.buscarPorNombre(nombre, pageable));
    }

    private Page<ClienteResponse> enriquecerConEstado(Page<PerfilCliente> page) {
        List<UUID> ids = page.getContent().stream().map(PerfilCliente::getIdUsuario).toList();
        Map<UUID, EstadoUsuario> estadoMap = usuarioAuthRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(UsuarioAuth::getIdUsuario, UsuarioAuth::getEstado));
        return page.map(c -> {
            ClienteResponse r = clienteMapper.toResponse(c);
            r.setEstadoUsuario(estadoMap.getOrDefault(c.getIdUsuario(), EstadoUsuario.ACTIVO));
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarClientePorDniRuc(String dniRuc) {
        PerfilCliente perfilCliente = perfilClienteRepository.findByDniRuc(dniRuc)
            .orElseThrow(() -> {
                log.debug("Cliente no encontrado con DNI/RUC: {}", maskDocumento(dniRuc));
                return ClienteNoEncontradoException.byDniRuc(maskDocumento(dniRuc));
            });

        return clienteMapper.toResponse(perfilCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCliente(UUID idUsuario) {
        return perfilClienteRepository.existsById(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeClientePorDniRuc(String dniRuc) {
        return perfilClienteRepository.existsByDniRuc(dniRuc);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTotalClientes() {
        return perfilClienteRepository.count();
    }

    private PerfilCliente obtenerPerfil(UUID idUsuario) {
        return perfilClienteRepository.findById(idUsuario)
            .orElseThrow(() -> {
                log.debug("Cliente no encontrado con idUsuario: {}", idUsuario);
                return ClienteNoEncontradoException.byIdUsuario(idUsuario);
            });
    }

    private void validarDniRucDisponible(String dniRuc) {
        if (dniRuc != null && perfilClienteRepository.existsByDniRuc(dniRuc)) {
            log.warn("Intento de crear perfil con DNI/RUC duplicado: {}", maskDocumento(dniRuc));
            throw DniDuplicadoException.byDniRuc(maskDocumento(dniRuc));
        }
    }

    /**
     * El DNI/RUC es dato personal: nunca se expone completo en logs ni en
     * mensajes de error. Se conservan los extremos para trazabilidad de soporte.
     */
    private static String maskDocumento(String documento) {
        if (documento == null || documento.length() < 5) {
            return "***";
        }
        return documento.substring(0, 2)
            + "*".repeat(documento.length() - 4)
            + documento.substring(documento.length() - 2);
    }
}
