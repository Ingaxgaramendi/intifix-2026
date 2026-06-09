package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.TechAprobacionRequest;
import com.intifix.modules.technicians.dto.TecnicoResponse;
import com.intifix.modules.technicians.service.TecnicoService;
import com.intifix.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/technicians")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TechAdminController {

    private final TecnicoService tecnicoService;

    @PatchMapping("/{id}/auditar")
    public ResponseEntity < ApiResponse < TecnicoResponse >> auditarTecnico(@PathVariable UUID id, @RequestBody TechAprobacionRequest request) {
        TecnicoResponse response = tecnicoService.procesarAprobacion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Auditoría procesada y estado de ENUM fiscal actualizado.", response));
    }
}
