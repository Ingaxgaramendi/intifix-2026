package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.*;
import com.intifix.modules.services.entity.*;
import com.intifix.modules.services.entity.enums.EstadoServicio;
import com.intifix.modules.services.entity.enums.ModalidadServicio;
import com.intifix.modules.services.entity.enums.PrioridadServicio;
import com.intifix.modules.services.entity.enums.TipoArchivo;
import com.intifix.modules.services.repository.*;
import com.intifix.modules.technicians.service.TecnicoService; // Tu módulo core de técnicos
import com.intifix.shared.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestionServicioServiceImpl implements GestionServicioService {

    private final ServicioRepository servicioRepository;
    private final AsignacionServicioRepository asignacionRepository;
    private final HistorialServicioRepository historialRepository;
    private final EvidenciaServicioRepository evidenciaRepository;
    private final CalificacionRepository calificacionRepository;
    private final TecnicoService tecnicoService; // Inyección crucial para actualizar la reputación

    @Override
    @Transactional
    public ServicioResponse publicarServicio(ServicioRequest request) {
        ModalidadServicio mod;
        PrioridadServicio pri;
        try {
            mod = ModalidadServicio.valueOf(request.getModalidad().toUpperCase());
            pri = PrioridadServicio.valueOf(request.getPrioridad().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("La modalidad o prioridad enviada no es válida para el negocio, mrd.");
        }

        Ubicacion ub = Ubicacion.builder()
        .departamento(request.getUbicacion().getDepartamento())
        .provincia(request.getUbicacion().getProvincia())
        .distrito(request.getUbicacion().getDistrito())
        .direccionTexto(request.getUbicacion().getDireccionTexto())
        .referencia(request.getUbicacion().getReferencia())
        .latitud(request.getUbicacion().getLatitud())
        .longitud(request.getUbicacion().getLongitud())
        .build();

        Servicio servicio = Servicio.builder()
        .idCliente(request.getIdCliente())
        .titulo(request.getTitulo())
        .descripcion(request.getDescripcion())
        .modalidad(mod)
        .prioridad(pri)
        .presupuestoMaximo(request.getPresupuestoMaximo())
        .fechaProgramada(request.getFechaProgramada())
        .estado(EstadoServicio.PENDIENTE)
        .ubicacion(ub)
        .build();

        Servicio guardado = servicioRepository.save(servicio);
        registrarHistorial(guardado, EstadoServicio.PENDIENTE, "Orden de servicio publicada en la plataforma por el cliente.");

        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public void registrarAsignacion(UUID idServicio, UUID idTecnico, UUID idCotizacion) {
        Servicio servicio = servicioRepository.findById(idServicio)
        .orElseThrow(() -> new CustomException("Servicio no localizado para asignar."));

        // Modificar el estado del servicio macro
        servicio.setEstado(EstadoServicio.ASIGNADO);
        servicioRepository.save(servicio);

        // Crear registro atómico en la tabla asignaciones_servicio
        AsignacionServicio asignacion = AsignacionServicio.builder()
        .servicio(servicio)
        .idUsuarioTecnico(idTecnico)
        .idCotizacion(idCotizacion) // Se guarda la referencia limpia del id de cotización
        .build();
        asignacionRepository.save(asignacion);

        registrarHistorial(servicio, EstadoServicio.ASIGNADO, "Pacto comercial cerrado. Orden asignada al técnico operativo.");
    }

    @Override
    @Transactional
    public void subirEvidenciaServicio(UUID idServicio, EvidenciaRequest request) {
        Servicio servicio = servicioRepository.findById(idServicio)
        .orElseThrow(() -> new CustomException("Servicio no encontrado para anexar evidencias, mrd."));

        TipoArchivo tipoEnum;
        try {
            tipoEnum = TipoArchivo.valueOf(request.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Formato de archivo inválido. Solo se acepta IMAGEN, VIDEO o PDF.");
        }

        EvidenciaServicio evidencia = EvidenciaServicio.builder()
        .servicio(servicio)
        .urlArchivo(request.getUrlArchivo())
        .tipo(tipoEnum)
        .descripcion(request.getDescripcion())
        .subidoPor(request.getSubidoPor())
        .build();

        evidenciaRepository.save(evidencia);
    }

    @Override
    @Transactional
    public void calificarYFinalizarServicio(UUID idServicio, CalificacionRequest request) {
        Servicio servicio = servicioRepository.findById(idServicio)
        .orElseThrow(() -> new CustomException("Servicio no existente para proceder al cierre."));

        if (EstadoServicio.FINALIZADO == servicio.getEstado()) {
            throw new CustomException("Este servicio ya fue finalizado y calificado previamente.");
        }

        // Buscamos quién fue el técnico asignado oficialmente mediante la tabla intermedia
        AsignacionServicio asignacion = asignacionRepository.findByServicioId(idServicio)
        .orElseThrow(() -> new CustomException("No se puede cerrar un servicio que no tiene una asignación oficial."));

        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new CustomException("La puntuación debe estar estrictamente en el rango de 1 a 5, carajo.");
        }

        // 1. Guardar la calificación oficial
        Calificacion calificacion = Calificacion.builder()
        .servicio(servicio)
        .idCliente(request.getIdCliente())
        .idUsuarioTecnico(asignacion.getIdUsuarioTecnico())
        .puntuacion(request.getPuntuacion())
        .comentario(request.getComentario())
        .build();
        calificacionRepository.save(calificacion);

        // 2. Transicionar estado macro del servicio a FINALIZADO
        servicio.setEstado(EstadoServicio.FINALIZADO);
        servicioRepository.save(servicio);

        registrarHistorial(servicio, EstadoServicio.FINALIZADO, "Servicio finalizado con éxito por el cliente. Puntuación: " + request.getPuntuacion());

        // 3. DISPARO DINÁMICO ATÓMICO: Recalcula en caliente el promedio del técnico en su tabla reputación
        tecnicoService.incrementarServicioCompleto(asignacion.getIdUsuarioTecnico(), request.getPuntuacion().doubleValue());
    }

    @Override
    @Transactional
    public void actualizarEstadoManual(UUID idServicio, String nuevoEstado, String comentario) {
        Servicio servicio = servicioRepository.findById(idServicio)
        .orElseThrow(() -> new CustomException("Servicio no existente para actualizar."));

        EstadoServicio estadoFormateado;
        try {
            estadoFormateado = EstadoServicio.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("El estado al que intentas mover el servicio no existe en el flujo base.");
        }

        servicio.setEstado(estadoFormateado);
        servicioRepository.save(servicio);

        registrarHistorial(servicio, estadoFormateado, comentario);
    }

    @Override
    @Transactional(readOnly = true)
    public List < ServicioResponse > listarDisponiblesParaCotizar() {
        return servicioRepository.findByEstado(EstadoServicio.PENDIENTE).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    }

    private void registrarHistorial(Servicio servicio, EstadoServicio estado, String comentario) {
        HistorialServicio hs = HistorialServicio.builder()
        .servicio(servicio)
        .estado(estado.name())
        .comentario(comentario)
        .build();
        historialRepository.save(hs);
    }

    private ServicioResponse mapToResponse(Servicio s) {
        return ServicioResponse.builder()
        .idServicio(s.getId())
        .idCliente(s.getIdCliente())
        .titulo(s.getTitulo())
        .descripcion(s.getDescripcion())
        .modalidad(s.getModalidad().name())
        .prioridad(s.getPrioridad().name())
        .estado(s.getEstado().name())
        .presupuestoMaximo(s.getPresupuestoMaximo())
        .fechaProgramada(s.getFechaProgramada())
        .direccionCompleta(s.getUbicacion().getDireccionTexto() + " - " + s.getUbicacion().getDistrito())
        .build();
    }
}
