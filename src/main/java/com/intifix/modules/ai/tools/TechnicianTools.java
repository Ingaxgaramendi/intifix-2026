package com.intifix.modules.ai.tools;

import com.intifix.modules.ai.dto.TecnicoRankeado;
import com.intifix.modules.technicians.dto.response.EspecialidadResponse;
import com.intifix.modules.technicians.dto.response.ReputacionResponse;
import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.entity.Especialidad;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import com.intifix.modules.technicians.repository.EspecialidadRepository;
import com.intifix.modules.technicians.service.EspecialidadService;
import com.intifix.modules.technicians.service.ReputacionTecnicoService;
import com.intifix.modules.technicians.service.TecnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Herramientas de Tool Calling para recuperar técnicos reales desde PostgreSQL.
 * Todas las consultas son tolerantes a fallos: devuelven lista vacía ante error.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TechnicianTools {

    private final TecnicoService tecnicoService;
    private final EspecialidadService especialidadService;
    private final EspecialidadRepository especialidadRepository;
    private final ReputacionTecnicoService reputacionService;

    private static final int LIMITE_BUSQUEDA = 10;
    private static final int TOP_RECOMENDACIONES = 3;

    @Tool(description = """
            Busca técnicos aprobados por categoría o especialidad.
            Acepta nombres parciales o coloquiales: "refrigeradoras", "fridge",
            "lavadora", "tele", "luz", "caño", etc.
            Devuelve los técnicos de la categoría más parecida.""")
    public List<TecnicoRankeado> findByCategory(
            @ToolParam(description = "Nombre o palabra clave de la categoría") String category) {
        return buscarPorCategoria(category).stream()
                .map(this::enriquecerConReputacion)
                .toList();
    }

    @Tool(description = """
            Devuelve los mejores técnicos para una categoría ordenados por
            calificación promedio (mayor a menor). Úsalo cuando el usuario
            pida recomendaciones o los "mejores" técnicos.
            Acepta nombres parciales: "refrigeradoras", "lavadoras", etc.""")
    public List<TecnicoRankeado> findTopRated(
            @ToolParam(description = "Nombre o palabra clave de la categoría") String category) {
        List<TecnicoResponse> candidatos = buscarPorCategoria(category);
        if (candidatos.isEmpty()) return Collections.emptyList();

        return candidatos.stream()
                .map(this::enriquecerConReputacion)
                .sorted(Comparator
                        .comparing((TecnicoRankeado t) ->
                                t.disponibilidad() == DisponibilidadTecnico.DISPONIBLE ? 0 : 1)
                        .thenComparing(TecnicoRankeado::promedioCalificacion,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(TOP_RECOMENDACIONES)
                .toList();
    }

    @Tool(description = """
            Busca técnicos a partir de la descripción de un problema técnico.
            Infiere la categoría más probable del problema y busca con coincidencia
            parcial (ej: "mi nevera no enfría" → busca "refrigeradoras").""")
    public List<TecnicoRankeado> findByProblem(
            @ToolParam(description = "Descripción del problema o categoría probable") String problem) {
        return buscarPorCategoria(problem).stream()
                .map(this::enriquecerConReputacion)
                .toList();
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Búsqueda tolerante: primero intenta match exacto; si falla, hace LIKE
     * insensible a mayúsculas y toma la primera especialidad que coincida.
     */
    private List<TecnicoResponse> buscarPorCategoria(String categoria) {
        UUID idEspecialidad = resolverEspecialidad(categoria);
        if (idEspecialidad == null) {
            log.warn("No se encontró especialidad para '{}' ni por coincidencia parcial", categoria);
            return Collections.emptyList();
        }
        try {
            return tecnicoService
                    .buscarTecnicosPorEspecialidad(idEspecialidad, PageRequest.of(0, LIMITE_BUSQUEDA))
                    .getContent()
                    .stream()
                    .filter(t -> t.getEstadoAprobacion() == EstadoAprobacionTecnico.APROBADO)
                    .toList();
        } catch (RuntimeException e) {
            log.warn("Error al buscar técnicos para especialidad {}: {}", idEspecialidad, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Resuelve el UUID de la especialidad usando búsqueda parcial tolerante.
     * 1. Intenta match exacto (vía EspecialidadService).
     * 2. Si falla, busca por LIKE con la palabra clave y retorna el primer resultado.
     */
    private UUID resolverEspecialidad(String keyword) {
        // 1. Match exacto
        try {
            EspecialidadResponse exacta = especialidadService.obtenerEspecialidadPorNombre(keyword);
            return exacta.getIdEspecialidad();
        } catch (RuntimeException ignored) {
            // no match exacto — continúa con búsqueda parcial
        }

        // 2. Búsqueda parcial LIKE (case-insensitive, ya definida en EspecialidadRepository)
        List<Especialidad> candidatas = especialidadRepository.buscarPorNombre(keyword);
        if (!candidatas.isEmpty()) {
            Especialidad mejor = candidatas.get(0);
            log.info("Match parcial: '{}' → '{}'", keyword, mejor.getNombre());
            return mejor.getIdEspecialidad();
        }

        // 3. Si la keyword tiene varias palabras, intenta con la primera significativa
        String[] palabras = keyword.toLowerCase().split("[\\s,y]+");
        for (String palabra : palabras) {
            if (palabra.length() < 3) continue;
            List<Especialidad> sub = especialidadRepository.buscarPorNombre(palabra);
            if (!sub.isEmpty()) {
                log.info("Match por token '{}': '{}'", palabra, sub.get(0).getNombre());
                return sub.get(0).getIdEspecialidad();
            }
        }

        return null;
    }

    private TecnicoRankeado enriquecerConReputacion(TecnicoResponse tecnico) {
        try {
            if (reputacionService.existeReputacion(tecnico.getIdUsuario())) {
                ReputacionResponse rep = reputacionService.obtenerReputacion(tecnico.getIdUsuario());
                return TecnicoRankeado.of(tecnico, rep);
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener reputación del técnico {}: {}",
                    tecnico.getIdUsuario(), e.getMessage());
        }
        return TecnicoRankeado.sinReputacion(tecnico);
    }
}
