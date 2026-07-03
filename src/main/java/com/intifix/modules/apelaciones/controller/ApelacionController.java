package com.intifix.modules.apelaciones.controller;

import com.intifix.modules.apelaciones.dto.ApelacionResponse;
import com.intifix.modules.apelaciones.dto.CrearApelacionRequest;
import com.intifix.modules.apelaciones.dto.RevisarApelacionRequest;
import com.intifix.modules.apelaciones.entity.EstadoApelacion;
import com.intifix.modules.apelaciones.service.ApelacionService;
import com.intifix.shared.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApelacionController {

    private final ApelacionService apelacionService;

    /** Endpoint público: cualquier usuario puede enviar un reclamo sin autenticación. */
    @PostMapping("/api/v1/auth/apelar")
    public ResponseEntity<ApiResponse<Void>> apelar(@Valid @RequestBody CrearApelacionRequest request) {
        apelacionService.crear(request);
        // Always 200 — don't reveal whether the correo exists.
        return ResponseEntity.ok(ApiResponse.success(
            "Tu reclamo fue recibido. El equipo de soporte lo revisará pronto.", null));
    }

    /** Lista de apelaciones — solo ADMIN. */
    @GetMapping("/api/v1/admin/apelaciones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ApelacionResponse>>> listar(
            @RequestParam(required = false) EstadoApelacion estado,
            @PageableDefault(size = 20, sort = "fechaEnvio", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("OK", apelacionService.listar(estado, pageable)));
    }

    /** Marcar una apelación como REVISADA o RESUELTA — solo ADMIN. */
    @PatchMapping("/api/v1/admin/apelaciones/{id}/revisar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApelacionResponse>> revisar(
            @PathVariable UUID id,
            @Valid @RequestBody RevisarApelacionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Apelación actualizada.", apelacionService.revisar(id, request)));
    }

    /** Badge de pendientes para el admin (dashboard). */
    @GetMapping("/api/v1/admin/apelaciones/pendientes/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> contarPendientes() {
        return ResponseEntity.ok(ApiResponse.success("OK", apelacionService.contarPendientes()));
    }
}
