package com.intifix.modules.geo.controller;

import com.intifix.modules.geo.dto.request.RegistrarUbicacionPublicaRequest;
import com.intifix.modules.geo.dto.response.UbicacionPublicaResponse;
import com.intifix.modules.geo.service.UbicacionService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Alta y consulta de ubicaciones. Cualquier usuario autenticado puede registrar
 * un punto del mapa para obtener su {@code idUbicacion}, que luego usan los
 * servicios ({@code POST /api/v1/services}) y el perfil del técnico.
 */
@RestController
@RequestMapping("/api/v1/ubicaciones")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ubicaciones", description = "Registro y consulta de ubicaciones (departamento/distrito + lat/lng).")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @PostMapping
    @Operation(summary = "Registrar ubicación",
            description = "Crea una ubicación a partir de los datos del mapa y devuelve su idUbicacion.")
    public ResponseEntity<ApiResponse<UbicacionPublicaResponse>> registrar(
            @Valid @RequestBody RegistrarUbicacionPublicaRequest request) {
        UbicacionPublicaResponse response = ubicacionService.registrar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ubicación registrada exitosamente.", response));
    }

    @GetMapping("/{idUbicacion}")
    @Operation(summary = "Obtener ubicación por id",
            description = "Devuelve los datos de una ubicación existente.")
    public ResponseEntity<ApiResponse<UbicacionPublicaResponse>> obtenerPorId(
            @PathVariable UUID idUbicacion) {
        UbicacionPublicaResponse response = ubicacionService.obtenerPorId(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Ubicación obtenida.", response));
    }
}
