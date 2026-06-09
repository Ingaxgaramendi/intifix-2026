package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.*;
import com.intifix.modules.technicians.service.TecnicoService;
import com.intifix.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TECNICO')")
public class TechOperativoController {

    private final TecnicoService tecnicoService;

    @GetMapping
    public ResponseEntity < ApiResponse < TecnicoResponse >> obtenerMiPerfil(@RequestParam UUID tecnicoId) {
        TecnicoResponse response = tecnicoService.obtenerPorId(tecnicoId);
        return ResponseEntity.ok(ApiResponse.success("Perfil técnico recuperado del core transaccional.", response));
    }

    @PutMapping
    public ResponseEntity < ApiResponse < TecnicoResponse >> actualizarMiPerfil(@RequestParam UUID tecnicoId, @RequestBody TechUpdateRequest request) {
        TecnicoResponse response = tecnicoService.actualizarPerfil(tecnicoId, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil y grilla de horarios actualizados correctamente.", response));
    }

    @PatchMapping("/disponibilidad")
    public ResponseEntity < ApiResponse < TecnicoResponse >> cambiarDisponibilidad(@RequestParam UUID tecnicoId, @RequestBody TechStatusUpdateRequest request) {
        TecnicoResponse response = tecnicoService.actualizarDisponibilidad(tecnicoId, request);
        return ResponseEntity.ok(ApiResponse.success("Estado de disponibilidad actualizado con éxito.", response));
    }
}
