package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.request.CrearExcepcionHorarioRequest;
import com.intifix.modules.technicians.dto.response.ExcepcionHorarioResponse;
import com.intifix.modules.technicians.service.ExcepcionHorarioTecnicoService;
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
@RequestMapping("/api/v1/technicians/schedule-exceptions")
@RequiredArgsConstructor
@Tag(name = "Excepciones de Horario", description = "API para gestión de excepciones de horario de técnicos")
public class ExcepcionHorarioController {

    private final ExcepcionHorarioTecnicoService excepcionHorarioTecnicoService;

    @PostMapping
    @Operation(summary = "Crear excepción de horario", description = "Crea una nueva excepción de horario para un técnico")
    public ResponseEntity<ApiResponse<ExcepcionHorarioResponse>> crearExcepcion(@Valid @RequestBody CrearExcepcionHorarioRequest request) {
        ExcepcionHorarioResponse response = excepcionHorarioTecnicoService.crearExcepcion(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Excepción de horario creada exitosamente.", response));
    }

    @DeleteMapping("/{idExcepcion}")
    @Operation(summary = "Eliminar excepción de horario", description = "Elimina una excepción de horario específica")
    public ResponseEntity<ApiResponse<Void>> eliminarExcepcion(@PathVariable UUID idExcepcion) {
        excepcionHorarioTecnicoService.eliminarExcepcion(idExcepcion);
        return ResponseEntity.ok(ApiResponse.success("Excepción de horario eliminada exitosamente.", null));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @Operation(summary = "Listar excepciones por técnico", description = "Obtiene todas las excepciones de horario de un técnico específico")
    public ResponseEntity<ApiResponse<List<ExcepcionHorarioResponse>>> listarExcepcionesPorTecnico(@PathVariable UUID idUsuarioTecnico) {
        List<ExcepcionHorarioResponse> response = excepcionHorarioTecnicoService.listarExcepcionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Excepciones del técnico obtenidas exitosamente.", response));
    }

    @GetMapping("/{idExcepcion}")
    @Operation(summary = "Obtener excepción por ID", description = "Obtiene una excepción de horario específica por su ID")
    public ResponseEntity<ApiResponse<ExcepcionHorarioResponse>> obtenerExcepcionPorId(@PathVariable UUID idExcepcion) {
        ExcepcionHorarioResponse response = excepcionHorarioTecnicoService.obtenerExcepcionPorId(idExcepcion);
        return ResponseEntity.ok(ApiResponse.success("Excepción de horario encontrada.", response));
    }

    @DeleteMapping("/tecnico/{idUsuarioTecnico}")
    @Operation(summary = "Eliminar todas las excepciones de un técnico", description = "Elimina todas las excepciones de horario asociadas a un técnico")
    public ResponseEntity<ApiResponse<Void>> eliminarExcepcionesPorTecnico(@PathVariable UUID idUsuarioTecnico) {
        excepcionHorarioTecnicoService.eliminarExcepcionesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Todas las excepciones del técnico eliminadas exitosamente.", null));
    }
}
