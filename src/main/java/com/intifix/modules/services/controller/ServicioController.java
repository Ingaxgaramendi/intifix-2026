package com.intifix.modules.services.controller;

import com.intifix.modules.services.dto.*;
import com.intifix.modules.services.service.GestionServicioService;
import com.intifix.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServicioController {

    private final GestionServicioService servicioService;

    // SOLO EL CLIENTE PUEDE REGISTRAR ÓRDENES DE TRABAJO
    @PostMapping("/publicar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity < ApiResponse < ServicioResponse >> crearOrden(@RequestBody ServicioRequest request) {
        ServicioResponse response = servicioService.publicarServicio(request);
        return ResponseEntity.ok(ApiResponse.success("¡Orden de servicio indexada correctamente, mi rey!", response));
    }

    // SOLO EL TÉCNICO ACCEDE AL RADAR PARA VER TRABAJOS LIBRES CERCANOS
    @GetMapping("/radar")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity < ApiResponse < List < ServicioResponse >>> listarDisponibles() {
        List < ServicioResponse > disponibles = servicioService.listarDisponiblesParaCotizar();
        return ResponseEntity.ok(ApiResponse.success("Trabajos listos para recibir ofertas localizados.", disponibles));
    }

    // AMBOS PUEDEN SUBIR FOTOS (El técnico como sustento y el cliente para mostrar el problema)
    @PostMapping("/{idServicio}/evidencias")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO')")
    public ResponseEntity < ApiResponse < String >> subirMultimedia(@PathVariable UUID idServicio, @RequestBody EvidenciaRequest request) {
        servicioService.subirEvidenciaServicio(idServicio, request);
        return ResponseEntity.ok(ApiResponse.success("Evidencia de campo registrada de forma segura.", null));
    }

    // SOLO EL CLIENTE CIERRA EL CONTRATO Y CALIFICA CON ESTRELLAS
    @PostMapping("/{idServicio}/calificar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity < ApiResponse < String >> finalizarYCalificar(@PathVariable UUID idServicio, @RequestBody CalificacionRequest request) {
        servicioService.calificarYFinalizarServicio(idServicio, request);
        return ResponseEntity.ok(ApiResponse.success("Servicio cerrado oficialmente. Reputación del técnico recalculada.", null));
    }

    // CONTROL DE ESTADOS GENÉRICO (Para flujos internos o cancelaciones rápidas)
    @PatchMapping("/{idServicio}/estado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO', 'ADMIN')")
    public ResponseEntity < ApiResponse < String >> cambiarEstadoManual(@PathVariable UUID idServicio, @RequestParam String nuevoEstado, @RequestParam String comentario) {
        servicioService.actualizarEstadoManual(idServicio, nuevoEstado, comentario);
        return ResponseEntity.ok(ApiResponse.success("Auditoría de estado actualizada.", null));
    }
}
