package com.intifix.modules.geo.service;

import com.intifix.modules.geo.dto.request.RegistrarUbicacionPublicaRequest;
import com.intifix.modules.geo.dto.response.UbicacionPublicaResponse;

import java.util.UUID;

/**
 * Alta y consulta de ubicaciones (tabla {@code ubicaciones}). Permite que el
 * frontend registre un punto del mapa y obtenga su {@code idUbicacion}, que luego
 * referencian los servicios ({@code CrearServicioRequest.idUbicacion}) y el perfil
 * del técnico.
 */
public interface UbicacionService {

    /** Registra una ubicación y devuelve sus datos, incluido el id generado. */
    UbicacionPublicaResponse registrar(RegistrarUbicacionPublicaRequest request);

    /** Obtiene una ubicación por su id; lanza ResourceNotFoundException si no existe. */
    UbicacionPublicaResponse obtenerPorId(UUID idUbicacion);
}
