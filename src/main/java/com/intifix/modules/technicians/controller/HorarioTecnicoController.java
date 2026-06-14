package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.request.ActualizarHorarioRequest;
import com.intifix.modules.technicians.dto.request.CrearHorarioRequest;
import com.intifix.modules.technicians.dto.response.HorarioResponse;
import com.intifix.modules.technicians.service.HorarioTecnicoService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians/schedules")
@RequiredArgsConstructor
@Tag(name = "Horarios de Técnicos", description = "API para gestión de horarios de técnicos")
public class HorarioTecnicoController {

    private final HorarioTecnicoService horarioTecnicoService;

    @PostMapping
    @Operation(summary = "Crear horario", description = "Crea un nuevo horario para un técnico")
    public ResponseEntity<ApiResponse<HorarioResponse>> crearHorario(@Valid @RequestBody CrearHorarioRequest request) {
        HorarioResponse response = horarioTecnicoService.crearHorario(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Horario creado exitosamente.", response));
    }

    @PutMapping("/{idHorario}")
    @Operation(summary = "Actualizar horario", description = "Actualiza un horario existente")
    public ResponseEntity<ApiResponse<HorarioResponse>> actualizarHorario(
            @PathVariable UUID idHorario,
            @Valid @RequestBody ActualizarHorarioRequest request) {
        HorarioResponse response = horarioTecnicoService.actualizarHorario(idHorario, request);
        return ResponseEntity.ok(ApiResponse.success("Horario actualizado exitosamente.", response));
    }

    @DeleteMapping("/{idHorario}")
    @Operation(summary = "Eliminar horario", description = "Elimina un horario específico")
    public ResponseEntity<ApiResponse<Void>> eliminarHorario(@PathVariable UUID idHorario) {
        horarioTecnicoService.eliminarHorario(idHorario);
        return ResponseEntity.ok(ApiResponse.success("Horario eliminado exitosamente.", null));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @Operation(summary = "Listar horarios por técnico", description = "Obtiene todos los horarios activos de un técnico específico")
    public ResponseEntity<ApiResponse<List<HorarioResponse>>> listarHorariosPorTecnico(@PathVariable UUID idUsuarioTecnico) {
        List<HorarioResponse> response = horarioTecnicoService.listarHorariosPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Horarios del técnico obtenidos exitosamente.", response));
    }

    @GetMapping("/{idHorario}")
    @Operation(summary = "Obtener horario por ID", description = "Obtiene un horario específico por su ID")
    public ResponseEntity<ApiResponse<HorarioResponse>> obtenerHorarioPorId(@PathVariable UUID idHorario) {
        HorarioResponse response = horarioTecnicoService.obtenerHorarioPorId(idHorario);
        return ResponseEntity.ok(ApiResponse.success("Horario encontrado.", response));
    }

    @DeleteMapping("/tecnico/{idUsuarioTecnico}")
    @Operation(summary = "Eliminar todos los horarios de un técnico", description = "Elimina todos los horarios asociados a un técnico")
    public ResponseEntity<ApiResponse<Void>> eliminarHorariosPorTecnico(@PathVariable UUID idUsuarioTecnico) {
        horarioTecnicoService.eliminarHorariosPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Todos los horarios del técnico eliminados exitosamente.", null));
    }
}
