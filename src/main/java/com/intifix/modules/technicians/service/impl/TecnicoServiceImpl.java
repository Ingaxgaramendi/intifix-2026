ackage com.intifix.modules.technicians.service.impl;

import com.intifix.modules.auth.entity.EstadoUsuario;
import com.intifix.modules.auth.entity.UsuarioAuth;
import com.intifix.modules.auth.repository.UsuarioAuthRepository;
import com.intifix.modules.technicians.dto.request.ActualizarTecnicoRequest;
import com.intifix.modules.technicians.dto.request.CambiarDisponibilidadRequest;
import com.intifix.modules.technicians.dto.request.CrearTecnicoRequest;
import com.intifix.modules.technicians.dto.response.TecnicoDetalleResponse;
import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.entity.PerfilTecnico;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import com.intifix.modules.technicians.exception.DniDuplicadoException;
import com.intifix.modules.technicians.exception.TecnicoNoEncontradoException;
import com.intifix.modules.technicians.exception.UbicacionInvalidaException;
import com.intifix.modules.technicians.exception.UbicacionNoEncontradaException;
import com.intifix.modules.technicians.gateway.GeolocationClient;
import com.intifix.modules.technicians.mapper.TecnicoMapper;
import com.intifix.modules.technicians.repository.PerfilTecnicoRepository;
import com.intifix.modules.technicians.service.TecnicoService;
import com.intifix.modules.audit.event.TechnicianApprovedEvent;
import com.intifix.shared.security.SecurityUtils;
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
public class TecnicoServiceImpl implements TecnicoService {

    private static final String MSG_UBICACION_NO_VALIDA = "Ubicación no válida para asignación a técnico: {}";

    private final PerfilTecnicoRepository perfilTecnicoRepository;
    private final TecnicoMapper tecnicoMapper;
    private final GeolocationClient geolocationClient;
    private final ApplicationEventPublisher eventPublisher;
    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    @Transactional
    public TecnicoResponse crearTecnico(CrearTecnicoRequest request) {
        log.info("Creando perfil técnico para idUsuario: {}", request.getIdUsuario());

        if (perfilTecnicoRepository.existsByIdUsuario(request.getIdUsuario())) {
            log.warn("Intento de crear perfil técnico para usuario existente: {}", request.getIdUsuario());
            throw TecnicoNoEncontradoException.byIdUsuario(request.getIdUsuario());
        }

        if (perfilTecnicoRepository.existsByDniRuc(request.getDniRuc())) {
            log.warn("Intento de crear perfil técnico con DNI/RUC duplicado: {}", request.getDniRuc());
            throw DniDuplicadoException.byDniRuc(request.getDniRuc());
        }

        if (!geolocationClient.existsLocation(request.getIdUbicacion())) {
            log.warn("Ubicación no encontrada al crear técnico: {}", request.getIdUbicacion());
            throw UbicacionNoEncontradaException.forTechnicianAssignment(request.getIdUbicacion());
        }

        if (!geolocationClient.canAssignToTechnician(request.getIdUbicacion())) {
            log.warn(MSG_UBICACION_NO_VALIDA, request.getIdUbicacion());
            throw UbicacionInvalidaException.notValidForAssignment(request.getIdUbicacion());
        }

        PerfilTecnico perfilTecnico = tecnicoMapper.toEntity(request);
        perfilTecnico.setIdUsuario(request.getIdUsuario());

        PerfilTecnico guardado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Perfil técnico creado exitosamente para idUsuario: {} con ubicación: {}", 
            guardado.getIdUsuario(), guardado.getIdUbicacion());

        return tecnicoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public TecnicoResponse obtenerTecnicoPorId(UUID idUsuario) {
        log.debug("Obteniendo técnico por idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado con idUsuario: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        TecnicoResponse response = tecnicoMapper.toResponse(perfilTecnico);
        usuarioAuthRepository.obtenerEstadoPorId(idUsuario).ifPresent(response::setEstadoUsuario);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TecnicoDetalleResponse obtenerDetalleTecnicoPorId(UUID idUsuario) {
        log.debug("Obteniendo detalle técnico por idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado con idUsuario: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        TecnicoDetalleResponse response = tecnicoMapper.toDetalleResponse(perfilTecnico);
        usuarioAuthRepository.obtenerEstadoPorId(idUsuario).ifPresent(response::setEstadoUsuario);
        return response;
    }

    @Override
    @Transactional
    public TecnicoResponse actualizarTecnico(UUID idUsuario, ActualizarTecnicoRequest request) {
        log.info("Actualizando perfil técnico para idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para actualización: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        if (request.getDniRuc() != null && !request.getDniRuc().isBlank()) {
            if (!request.getDniRuc().equals(perfilTecnico.getDniRuc())) {
                if (perfilTecnicoRepository.existsByDniRuc(request.getDniRuc())) {
                    log.warn("Intento de actualizar con DNI/RUC duplicado: {}", request.getDniRuc());
                    throw DniDuplicadoException.byDniRuc(request.getDniRuc());
                }
            }
        }

        if (request.getIdUbicacion() != null && !request.getIdUbicacion().equals(perfilTecnico.getIdUbicacion())) {
            if (!geolocationClient.existsLocation(request.getIdUbicacion())) {
                log.warn("Ubicación no encontrada al actualizar técnico: {}", request.getIdUbicacion());
                throw UbicacionNoEncontradaException.forTechnicianAssignment(request.getIdUbicacion());
            }

            if (!geolocationClient.canAssignToTechnician(request.getIdUbicacion())) {
                log.warn(MSG_UBICACION_NO_VALIDA, request.getIdUbicacion());
                throw UbicacionInvalidaException.notValidForAssignment(request.getIdUbicacion());
            }
            log.info("Cambiando ubicación del técnico {} de {} a {}", 
                idUsuario, perfilTecnico.getIdUbicacion(), request.getIdUbicacion());
        }

        tecnicoMapper.updateEntityFromDto(request, perfilTecnico);

        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Perfil técnico actualizado exitosamente para idUsuario: {}", actualizado.getIdUsuario());

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminarTecnico(UUID idUsuario) {
        log.info("Eliminando perfil técnico para idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para eliminación: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        perfilTecnicoRepository.delete(perfilTecnico);
        log.info("Perfil técnico eliminado exitosamente para idUsuario: {}", idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TecnicoResponse> obtenerTodosTecnicos(Pageable pageable) {
        log.debug("Obteniendo todos los técnicos paginados");
        Page<PerfilTecnico> tecnicos = perfilTecnicoRepository.findAll(pageable);
        return enriquecerConEstado(tecnicos);
    }

    @Override
    @Transactional(readOnly = true)
    public TecnicoResponse buscarTecnicoPorDniRuc(String dniRuc) {
        log.debug("Buscando técnico por DNI/RUC: {}", dniRuc);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByDniRuc(dniRuc)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado con DNI/RUC: {}", dniRuc);
                return TecnicoNoEncontradoException.byDniRuc(dniRuc);
            });

        return tecnicoMapper.toResponse(perfilTecnico);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TecnicoResponse> buscarTecnicosPorDisponibilidad(DisponibilidadTecnico disponibilidad, Pageable pageable) {
        log.debug("Buscando técnicos por disponibilidad: {}", disponibilidad);

        Page<PerfilTecnico> tecnicos = perfilTecnicoRepository.findByDisponibilidad(disponibilidad, pageable);

        return tecnicos.map(tecnicoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TecnicoResponse> buscarTecnicosPorEspecialidad(UUID idEspecialidad, Pageable pageable) {
        log.debug("Buscando técnicos por especialidad: {}", idEspecialidad);

        Page<PerfilTecnico> tecnicos = perfilTecnicoRepository.buscarPorEspecialidad(idEspecialidad, pageable);

        return tecnicos.map(tecnicoMapper::toResponse);
    }

    @Override
    @Transactional
    public TecnicoResponse aprobarTecnico(UUID idUsuario) {
        log.info("Aprobando técnico con idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para aprobación: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        perfilTecnico.setEstadoAprobacion(EstadoAprobacionTecnico.APROBADO);
        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Técnico aprobado exitosamente para idUsuario: {}", actualizado.getIdUsuario());

        eventPublisher.publishEvent(new TechnicianApprovedEvent(
            actualizado.getIdUsuario(),
            SecurityUtils.currentUserId()
        ));

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public TecnicoResponse rechazarTecnico(UUID idUsuario) {
        log.info("Rechazando técnico con idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para rechazo: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        perfilTecnico.setEstadoAprobacion(EstadoAprobacionTecnico.RECHAZADO);
        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Técnico rechazado exitosamente para idUsuario: {}", actualizado.getIdUsuario());

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public TecnicoResponse cambiarDisponibilidad(UUID idUsuario, CambiarDisponibilidadRequest request) {
        log.info("Cambiando disponibilidad para idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para cambio de disponibilidad: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        // El técnico puede marcarse disponible/ocupado aunque su cuenta esté
        // PENDIENTE: igualmente no aparece en búsquedas hasta ser APROBADO
        // (el buscador filtra por aprobado + disponible).
        perfilTecnico.setDisponibilidad(request.getDisponibilidad());
        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Disponibilidad cambiada exitosamente para idUsuario: {}", actualizado.getIdUsuario());

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public TecnicoResponse actualizarDocumentos(UUID idUsuario, ActualizarTecnicoRequest request) {
        log.info("Actualizando documentos para idUsuario: {}", idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para actualización de documentos: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        tecnicoMapper.updateEntityFromDto(request, perfilTecnico);

        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Documentos actualizados exitosamente para idUsuario: {}", actualizado.getIdUsuario());

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TecnicoResponse> buscarTecnicosPorEstado(EstadoAprobacionTecnico estado, Pageable pageable) {
        log.debug("Buscando técnicos por estado: {}", estado);
        Page<PerfilTecnico> tecnicos = perfilTecnicoRepository.findByEstadoAprobacion(estado, pageable);
        return enriquecerConEstado(tecnicos);
    }

    private Page<TecnicoResponse> enriquecerConEstado(Page<PerfilTecnico> page) {
        List<UUID> ids = page.getContent().stream().map(PerfilTecnico::getIdUsuario).toList();
        Map<UUID, EstadoUsuario> estadoMap = usuarioAuthRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(UsuarioAuth::getIdUsuario, UsuarioAuth::getEstado));
        return page.map(t -> {
            TecnicoResponse r = tecnicoMapper.toResponse(t);
            r.setEstadoUsuario(estadoMap.getOrDefault(t.getIdUsuario(), EstadoUsuario.ACTIVO));
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTecnico(UUID idUsuario) {
        log.debug("Verificando existencia de técnico por idUsuario: {}", idUsuario);
        return perfilTecnicoRepository.existsByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTecnicoPorDniRuc(String dniRuc) {
        log.debug("Verificando existencia de técnico por DNI/RUC: {}", dniRuc);
        return perfilTecnicoRepository.existsByDniRuc(dniRuc);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTotalTecnicos() {
        log.debug("Contando total de técnicos");
        return perfilTecnicoRepository.contarTotalTecnicos();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosAprobados() {
        log.debug("Contando técnicos aprobados");
        return perfilTecnicoRepository.contarTecnicosAprobados();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosActivos() {
        log.debug("Contando técnicos activos");
        return perfilTecnicoRepository.contarTecnicosActivos();
    }

    @Override
    @Transactional
    public TecnicoResponse asignarUbicacion(UUID idUsuario, UUID idUbicacion) {
        log.info("Asignando ubicación {} al técnico {}", idUbicacion, idUsuario);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para asignación de ubicación: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        if (!geolocationClient.existsLocation(idUbicacion)) {
            log.warn("Ubicación no encontrada para asignación: {}", idUbicacion);
            throw UbicacionNoEncontradaException.forTechnicianAssignment(idUbicacion);
        }

        if (!geolocationClient.canAssignToTechnician(idUbicacion)) {
            log.warn(MSG_UBICACION_NO_VALIDA, idUbicacion);
            throw UbicacionInvalidaException.notValidForAssignment(idUbicacion);
        }

        perfilTecnico.setIdUbicacion(idUbicacion);
        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Ubicación asignada exitosamente al técnico {}: {}", idUsuario, idUbicacion);

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public TecnicoResponse actualizarUbicacion(UUID idUsuario, UUID idUbicacion) {
        log.info("Actualizando ubicación del técnico {} a {}", idUsuario, idUbicacion);

        PerfilTecnico perfilTecnico = perfilTecnicoRepository.findByIdUsuario(idUsuario)
            .orElseThrow(() -> {
                log.warn("Técnico no encontrado para actualización de ubicación: {}", idUsuario);
                return TecnicoNoEncontradoException.byIdUsuario(idUsuario);
            });

        if (!geolocationClient.existsLocation(idUbicacion)) {
            log.warn("Ubicación no encontrada para actualización: {}", idUbicacion);
            throw UbicacionNoEncontradaException.forTechnicianAssignment(idUbicacion);
        }

        if (!geolocationClient.canAssignToTechnician(idUbicacion)) {
            log.warn("Ubicación no válida para actualización de técnico: {}", idUbicacion);
            throw UbicacionInvalidaException.notValidForAssignment(idUbicacion);
        }

        UUID ubicacionAnterior = perfilTecnico.getIdUbicacion();
        perfilTecnico.setIdUbicacion(idUbicacion);
        PerfilTecnico actualizado = perfilTecnicoRepository.save(perfilTecnico);
        log.info("Ubicación actualizada exitosamente para técnico {}: de {} a {}", 
            idUsuario, ubicacionAnterior, idUbicacion);

        return tecnicoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponse> obtenerTecnicosPorUbicacion(UUID idUbicacion) {
        log.debug("Obteniendo técnicos por ubicación: {}", idUbicacion);
        List<PerfilTecnico> tecnicos = perfilTecnicoRepository.findByIdUbicacion(idUbicacion);
        return tecnicos.stream()
            .map(tecnicoMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponse> obtenerTecnicosDisponiblesPorUbicacion(UUID idUbicacion) {
        log.debug("Obteniendo técnicos disponibles por ubicación: {}", idUbicacion);
        List<PerfilTecnico> tecnicos = perfilTecnicoRepository.buscarTecnicosDisponiblesPorUbicacion(idUbicacion);
        return tecnicos.stream()
            .map(tecnicoMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponse> obtenerTecnicosAprobadosPorUbicacion(UUID idUbicacion) {
        log.debug("Obteniendo técnicos aprobados por ubicación: {}", idUbicacion);
        List<PerfilTecnico> tecnicos = perfilTecnicoRepository.buscarTecnicosAprobadosPorUbicacion(idUbicacion);
        return tecnicos.stream()
            .map(tecnicoMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponse> obtenerTecnicosDisponiblesYAprobadosPorUbicacion(UUID idUbicacion) {
        log.debug("Obteniendo técnicos disponibles y aprobados por ubicación: {}", idUbicacion);
        List<PerfilTecnico> tecnicos = perfilTecnicoRepository.buscarTecnicosDisponiblesYAprobadosPorUbicacion(idUbicacion);
        return tecnicos.stream()
            .map(tecnicoMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosPorUbicacion(UUID idUbicacion) {
        log.debug("Contando técnicos por ubicación: {}", idUbicacion);
        return perfilTecnicoRepository.contarTecnicosPorUbicacion(idUbicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosDisponiblesPorUbicacion(UUID idUbicacion) {
        log.debug("Contando técnicos disponibles por ubicación: {}", idUbicacion);
        return perfilTecnicoRepository.contarTecnicosDisponiblesPorUbicacion(idUbicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosAprobadosPorUbicacion(UUID idUbicacion) {
        log.debug("Contando técnicos aprobados por ubicación: {}", idUbicacion);
        return perfilTecnicoRepository.contarTecnicosAprobadosPorUbicacion(idUbicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTecnicosDisponiblesYAprobadosPorUbicacion(UUID idUbicacion) {
        log.debug("Contando técnicos disponibles y aprobados por ubicación: {}", idUbicacion);
        return perfilTecnicoRepository.contarTecnicosDisponiblesYAprobadosPorUbicacion(idUbicacion);
    }
}
