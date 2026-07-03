package com.intifix.modules.ai.tools;

import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.service.ServicioService;
import com.intifix.modules.technicians.entity.Especialidad;
import com.intifix.modules.technicians.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Herramientas para consultar servicios y especialidades del marketplace.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceTools {

    private final ServicioService servicioService;
    private final EspecialidadRepository especialidadRepository;

    private static final int LIMITE_BUSQUEDA = 10;

    @Tool(description = """
            Lista ÚNICAMENTE las categorías que tienen técnicos aprobados disponibles
            en este momento. Úsala cuando el usuario pregunte qué categorías hay,
            qué servicios ofrece la plataforma, o cuando una búsqueda devuelve vacío
            y quieres sugerir alternativas con técnicos reales.""")
    public List<String> listAllCategories() {
        try {
            List<String> conTecnicos = especialidadRepository.findConTecnicosAprobados()
                    .stream()
                    .map(Especialidad::getNombre)
                    .toList();
            if (!conTecnicos.isEmpty()) return conTecnicos;
            // fallback: si la query falla o devuelve vacío, lista todas
            return especialidadRepository.findAll()
                    .stream()
                    .map(Especialidad::getNombre)
                    .sorted()
                    .toList();
        } catch (RuntimeException e) {
            log.warn("No se pudieron listar las categorías con técnicos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Tool(description = """
            Busca servicios por tipo o palabra clave en el título
            (ej: "instalación", "reparación de fugas").""")
    public List<ServicioResponse> getServicesByType(
            @ToolParam(description = "Tipo o palabra clave del servicio") String type) {
        try {
            return servicioService
                    .buscarServiciosPorTitulo(type, PageRequest.of(0, LIMITE_BUSQUEDA))
                    .getContent();
        } catch (RuntimeException e) {
            log.warn("No se pudieron obtener servicios para el tipo '{}': {}", type, e.getMessage());
            return Collections.emptyList();
        }
    }
}
