package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.response.ReputacionResponse;
import com.intifix.modules.technicians.service.ReputacionTecnicoService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians/reputation")
@RequiredArgsConstructor
@Tag(name = "Reputación de Técnicos", description = "API para gestión de reputación de técnicos")
public class ReputacionController {

    private final ReputacionTecnicoService reputacionTecnicoService;

    @GetMapping("/{idUsuarioTecnico}")
    @Operation(summary = "Obtener reputación", description = "Obtiene la reputación actual de un técnico")
    public ResponseEntity<ApiResponse<ReputacionResponse>> obtenerReputacion(@PathVariable UUID idUsuarioTecnico) {
        ReputacionResponse response = reputacionTecnicoService.obtenerReputacion(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Reputación del técnico obtenida exitosamente.", response));
    }

    @PatchMapping("/{idUsuarioTecnico}/actualizar")
    @Operation(summary = "Actualizar reputación manualmente", description = "Actualiza manualmente la reputación de un técnico")
    public ResponseEntity<ApiResponse<ReputacionResponse>> actualizarReputacion(
            @PathVariable UUID idUsuarioTecnico,
            @RequestParam BigDecimal promedioCalificacion,
            @RequestParam Integer totalResenas) {
        ReputacionResponse response = reputacionTecnicoService.actualizarReputacion(idUsuarioTecnico, promedioCalificacion, totalResenas);
        return ResponseEntity.ok(ApiResponse.success("Reputación actualizada exitosamente.", response));
    }

    @PatchMapping("/{idUsuarioTecnico}/incrementar-servicios")
    @Operation(summary = "Incrementar servicios completados", description = "Incrementa el contador de servicios completados de un técnico")
    public ResponseEntity<ApiResponse<ReputacionResponse>> incrementarServiciosCompletados(@PathVariable UUID idUsuarioTecnico) {
        ReputacionResponse response = reputacionTecnicoService.incrementarServiciosCompletados(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Servicios completados incrementados exitosamente.", response));
    }

    @PatchMapping("/{idUsuarioTecnico}/actualizar-calificacion")
    @Operation(summary = "Actualizar promedio de calificaciones", description = "Actualiza el promedio de calificaciones con una nueva reseña")
    public ResponseEntity<ApiResponse<ReputacionResponse>> actualizarPromedioCalificaciones(
            @PathVariable UUID idUsuarioTecnico,
            @RequestParam BigDecimal nuevaCalificacion) {
        ReputacionResponse response = reputacionTecnicoService.actualizarPromedioCalificaciones(idUsuarioTecnico, nuevaCalificacion);
        return ResponseEntity.ok(ApiResponse.success("Promedio de calificaciones actualizado exitosamente.", response));
    }

    @PostMapping("/{idUsuarioTecnico}/inicializar")
    @Operation(summary = "Inicializar reputación", description = "Inicializa la reputación de un técnico con valores por defecto")
    public ResponseEntity<ApiResponse<ReputacionResponse>> inicializarReputacion(@PathVariable UUID idUsuarioTecnico) {
        ReputacionResponse response = reputacionTecnicoService.inicializarReputacion(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Reputación inicializada exitosamente.", response));
    }

    @GetMapping("/{idUsuarioTecnico}/existe")
    @Operation(summary = "Verificar existencia de reputación", description = "Verifica si existe reputación para un técnico")
    public ResponseEntity<ApiResponse<Boolean>> existeReputacion(@PathVariable UUID idUsuarioTecnico) {
        boolean existe = reputacionTecnicoService.existeReputacion(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Verificación de existencia completada.", existe));
    }
}
