package com.intifix.modules.technicians.service;

import com.intifix.modules.technicians.dto.*;
import com.intifix.modules.technicians.entity.*;
import com.intifix.modules.technicians.repository.EspecialidadRepository;
import com.intifix.modules.technicians.repository.PerfilTecnicoOperativoRepository;
import com.intifix.shared.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TecnicoServiceImpl implements TecnicoService {

    private final PerfilTecnicoOperativoRepository perfilRepository;
    private final EspecialidadRepository especialidadRepository;

    @Override
    @Transactional
    public TecnicoResponse inicializarPerfil(UUID usuarioId, String nombresCompletos, String dniRuc, BigDecimal tarifaBase, String dniFrontal, String dniTrasero, String antecedentes) {
        if (perfilRepository.existsById(usuarioId)) {
            throw new CustomException("El perfil del técnico ya se encuentra inicializado en el sistema.");
        }

        // 1. Instanciamos el perfil maestro con los datos requeridos obligatorios de tu SQL
        PerfilTecnicoOperativo perfil = PerfilTecnicoOperativo.builder()
        .usuarioId(usuarioId)
        .nombresCompletos(nombresCompletos)
        .dniRuc(dniRuc)
        .tarifaBase(tarifaBase)
        .dniFrontalUrl(dniFrontal)
        .dniTraseroUrl(dniTrasero)
        .referentePenalUrl(antecedentes)
        .estadoAprobacion("PENDIENTE")
        .disponibilidad("DISPONIBLE")
        .build();

        // 2. Instanciamos la reputación amarrándola al perfil en memoria antes de salvar
        ReputacionTecnico reputacion = ReputacionTecnico.builder()
        .tecnico(perfil)
        .idUsuarioTecnico(usuarioId) // Comparte la misma PK exacta de forma atómica
        .promedio(0.00)
        .totalResenas(0)
        .totalServicios(0)
        .actualizadoEn(OffsetDateTime.now())
        .build();

        perfil.setReputacion(reputacion);

        // 3. Al guardar el perfil, Hibernate guardará en cascada la reputación en la tabla 'reputacion_tecnico'
        return mapToResponse(perfilRepository.save(perfil));
    }

    @Override
    @Transactional(readOnly = true)
    public TecnicoResponse obtenerPorId(UUID usuarioId) {
        PerfilTecnicoOperativo perfil = perfilRepository.findById(usuarioId)
        .orElseThrow(() -> new CustomException("No se encontró ningún perfil técnico registrado con ese ID."));
        return mapToResponse(perfil);
    }

    @Override
    @Transactional
    public TecnicoResponse actualizarPerfil(UUID usuarioId, TechUpdateRequest request) {
        PerfilTecnicoOperativo perfil = perfilRepository.findById(usuarioId)
        .orElseThrow(() -> new CustomException("Perfil técnico no localizado."));

        if (request.getNombresCompletos() != null) perfil.setNombresCompletos(request.getNombresCompletos());
        if (request.getExperienciaAnios() != null) perfil.setExperienciaAnios(request.getExperienciaAnios());
        if (request.getTarifaBase() != null) perfil.setTarifaBase(request.getTarifaBase());

        if (request.getEspecialidadesIds() != null) {
            Set < Especialidad > especialidades = new HashSet <> (especialidadRepository.findAllById(request.getEspecialidadesIds()));
            perfil.setEspecialidades(especialidades);
        }

        if (request.getHorarios() != null) {
            perfil.getHorarios().clear();
            Set < HorarioTecnico > nuevosHorarios = request.getHorarios().stream()
            .map(dto -> {
                if (dto.getDiaSemana() < 0 || dto.getDiaSemana() > 6) {
                    throw new CustomException("El día de la semana debe ser entero entre 0 y 6.");
                }
                if (dto.getHoraInicio().isAfter(dto.getHoraFin())) {
                    throw new CustomException("La hora de inicio no puede superar a la de fin.");
                }
                return HorarioTecnico.builder()
                .tecnico(perfil)
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
            }).collect(Collectors.toSet());
            perfil.getHorarios().addAll(nuevosHorarios);
        }

        return mapToResponse(perfilRepository.save(perfil));
    }

    @Override
    @Transactional
    public TecnicoResponse actualizarDisponibilidad(UUID usuarioId, TechStatusUpdateRequest request) {
        PerfilTecnicoOperativo perfil = perfilRepository.findById(usuarioId)
        .orElseThrow(() -> new CustomException("Técnico inexistente."));

        if (!"APROBADO".equals(perfil.getEstadoAprobacion())) {
            throw new CustomException("Operación denegada. El perfil técnico debe estar APROBADO por administración central.");
        }

        String disp = request.getDisponibilidad().toUpperCase();
        if (!"DISPONIBLE".equals(disp) && !"OCUPADO".equals(disp)) {
            throw new CustomException("Valor de disponibilidad inválido para el ecosistema de INTIFIX.");
        }

        perfil.setDisponibilidad(disp);
        return mapToResponse(perfilRepository.save(perfil));
    }

    @Override
    @Transactional
    public TecnicoResponse procesarAprobacion(UUID usuarioId, TechAprobacionRequest request) {
        PerfilTecnicoOperativo perfil = perfilRepository.findById(usuarioId)
        .orElseThrow(() -> new CustomException("Perfil técnico no localizado para auditar."));

        String estado = request.getEstadoAprobacion().toUpperCase();
        if (!"APROBADO".equals(estado) && !"RECHAZADO".equals(estado) && !"PENDIENTE".equals(estado)) {
            throw new CustomException("Estado de aprobación desconocido.");
        }

        perfil.setEstadoAprobacion(estado);
        if (!"APROBADO".equals(estado)) {
            perfil.setDisponibilidad("OCUPADO");
        }
        return mapToResponse(perfilRepository.save(perfil));
    }

    // LOGICA CRITICA: Cada vez que culmine un servicio, el modulo de 'services' invocará este método
    @Override
    @Transactional
    public void incrementarServicioCompleto(UUID usuarioId, Double nuevaCalificacion) {
        PerfilTecnicoOperativo perfil = perfilRepository.findById(usuarioId)
        .orElseThrow(() -> new CustomException("Error interno al procesar reputación."));

        ReputacionTecnico rep = perfil.getReputacion();
        int totalServiciosNuevos = rep.getTotalServicios() + 1;
        int totalResenasNuevas = rep.getTotalResenas() + 1;

        // Algoritmo matemático para promedio dinámico sin perder decimales empresariales
        double promedioNuevo = ((rep.getPromedio() * rep.getTotalResenas()) + nuevaCalificacion) / totalResenasNuevas;

        rep.setTotalServicios(totalServiciosNuevos);
        rep.setTotalResenas(totalResenasNuevas);
        rep.setPromedio(promedioNuevo);
        rep.setActualizadoEn(OffsetDateTime.now());

        perfilRepository.save(perfil);
    }

    private TecnicoResponse mapToResponse(PerfilTecnicoOperativo p) {
        Set < String > espNames = p.getEspecialidades().stream().map(Especialidad::getNombre).collect(Collectors.toSet());
        Set < HorarioDto > horariosDto = p.getHorarios().stream()
        .map(h -> HorarioDto.builder()
        .diaSemana(h.getDiaSemana())
        .horaInicio(h.getHoraInicio())
        .horaFin(h.getHoraFin())
        .activo(h.getActivo())
        .build()).collect(Collectors.toSet());

        return TecnicoResponse.builder()
        .usuarioId(p.getUsuarioId())
        .nombresCompletos(p.getNombresCompletos())
        .dniRuc(p.getDniRuc())
        .experienciaAnios(p.getExperienciaAnios())
        .estadoAprobacion(p.getEstadoAprobacion())
        .disponibilidad(p.getDisponibilidad())
        .tarifaBase(p.getTarifaBase())
        .reputacionPromedio(p.getReputacion() != null ? p.getReputacion().getPromedio() : 0.00)
        .totalResenas(p.getReputacion() != null ? p.getReputacion().getTotalResenas() : 0)
        .totalServicios(p.getReputacion() != null ? p.getReputacion().getTotalServicios() : 0)
        .especialidades(espNames)
        .horarios(horariosDto)
        .build();
    }
}
