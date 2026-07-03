package com.intifix.modules.ai.tools;

import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.service.ServicioService;
import com.intifix.modules.users.dto.response.ClienteResponse;
import com.intifix.modules.users.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Herramientas para recuperar información del cliente y su historial de
 * servicios desde PostgreSQL.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserTools {

    private final ClienteService clienteService;
    private final ServicioService servicioService;

    private static final int LIMITE_HISTORIAL = 10;

    @Tool(description = "Obtiene la información básica de un cliente a partir de su identificador (UUID).")
    public ClienteResponse getUserInfo(
            @ToolParam(description = "UUID del cliente") String userId) {
        UUID id = parseUuid(userId);
        if (id == null) {
            return null;
        }
        try {
            return clienteService.obtenerClientePorId(id);
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el cliente {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Tool(description = "Obtiene el historial reciente de servicios solicitados por un cliente (UUID).")
    public List<ServicioResponse> getHistory(
            @ToolParam(description = "UUID del cliente") String userId) {
        UUID id = parseUuid(userId);
        if (id == null) {
            return Collections.emptyList();
        }
        try {
            return servicioService
                    .obtenerServiciosPorCliente(id, PageRequest.of(0, LIMITE_HISTORIAL))
                    .getContent();
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el historial del cliente {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Identificador de usuario inválido: '{}'", value);
            return null;
        }
    }
}
