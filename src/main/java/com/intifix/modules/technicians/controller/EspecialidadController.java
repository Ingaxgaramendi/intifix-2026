package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.request.ActualizarCertificadoEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.ActualizarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.AsignarEspecialidadRequest;
import com.intifix.modules.technicians.dto.request.CrearEspecialidadRequest;
import com.intifix.modules.technicians.dto.response.EspecialidadResponse;
import com.intifix.modules.technicians.dto.response.EspecialidadTecnicoResponse;
import com.intifix.modules.technicians.service.EspecialidadService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians/specialties")
@RequiredArgsConstructor
@Tag(name = "Especialidades", description = "API para gestión de especialidades técnicas")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @PostMapping
    @Operation(summary = "Crear especialidad", description = "Crea una nueva especialidad en el sistema")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> crearEspecialidad(@Valid @RequestBody CrearEspecialidadRequest request) {
        EspecialidadResponse response = especialidadService.crearEspecialidad(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Especialidad creada exitosamente.", response));
    }

    @PutMapping("/{idEspecialidad}")
    @Operation(summary = "Actualizar especialidad", description = "Actualiza una especialidad existente")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> actualizarEspecialidad(
            @PathVariable UUID idEspecialidad,
            @Valid @RequestBody ActualizarEspecialidadRequest request) {
        EspecialidadResponse response = especialidadService.actualizarEspecialidad(idEspecialidad, request);
        return ResponseEntity.ok(ApiResponse.success("Especialidad actualizada exitosamente.", response));
    }

    @DeleteMapping("/{idEspecialidad}")
    @Operation(summary = "Eliminar especialidad", description = "Elimina una especialidad del sistema")
    public ResponseEntity<ApiResponse<Void>> eliminarEspecialidad(@PathVariable UUID idEspecialidad) {
        especialidadService.eliminarEspecialidad(idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Especialidad eliminada exitosamente.", null));
    }

    @GetMapping
    @Operation(summary = "Listar todas las especialidades", description = "Obtiene todas las especialidades registradas en el sistema")
    public ResponseEntity<ApiResponse<List<EspecialidadResponse>>> listarEspecialidades() {
        List<EspecialidadResponse> response = especialidadService.listarEspecialidades();
        return ResponseEntity.ok(ApiResponse.success("Lista de especialidades obtenida exitosamente.", response));
    }

    @GetMapping("/{idEspecialidad}")
    @Operation(summary = "Obtener especialidad por ID", description = "Obtiene una especialidad específica por su ID")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> obtenerEspecialidadPorId(@PathVariable UUID idEspecialidad) {
        EspecialidadResponse response = especialidadService.obtenerEspecialidadPorId(idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Especialidad encontrada.", response));
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Obtener especialidad por nombre", description = "Obtiene una especialidad específica por su nombre")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> obtenerEspecialidadPorNombre(@PathVariable String nombre) {
        EspecialidadResponse response = especialidadService.obtenerEspecialidadPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success("Especialidad encontrada.", response));
    }

    @PostMapping("/asignar")
    @Operation(summary = "Asignar especialidad a técnico", description = "Asigna una especialidad a un técnico específico")
    public ResponseEntity<ApiResponse<Void>> asignarEspecialidadATecnico(@Valid @RequestBody AsignarEspecialidadRequest request) {
        especialidadService.asignarEspecialidadATecnico(request);
        return ResponseEntity.ok(ApiResponse.success("Especialidad asignada al técnico exitosamente.", null));
    }

    @DeleteMapping("/tecnico/{idUsuarioTecnico}/especialidad/{idEspecialidad}")
    @Operation(summary = "Remover especialidad de técnico", description = "Remueve una especialidad de un técnico específico")
    public ResponseEntity<ApiResponse<Void>> removerEspecialidadDeTecnico(
            @PathVariable UUID idUsuarioTecnico,
            @PathVariable UUID idEspecialidad) {
        especialidadService.removerEspecialidadDeTecnico(idUsuarioTecnico, idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Especialidad removida del técnico exitosamente.", null));
    }

    @GetMapping("/tecnico/{idUsuarioTecnico}")
    @Operation(summary = "Listar especialidades por técnico", description = "Obtiene todas las especialidades de un técnico (con su certificado)")
    public ResponseEntity<ApiResponse<List<EspecialidadTecnicoResponse>>> listarEspecialidadesPorTecnico(@PathVariable UUID idUsuarioTecnico) {
        List<EspecialidadTecnicoResponse> response = especialidadService.listarEspecialidadesPorTecnico(idUsuarioTecnico);
        return ResponseEntity.ok(ApiResponse.success("Especialidades del técnico obtenidas exitosamente.", response));
    }

    @PatchMapping("/tecnico/{idUsuarioTecnico}/especialidad/{idEspecialidad}/certificado")
    @Operation(summary = "Actualizar certificado de especialidad", description = "Sube/actualiza el certificado que acredita una especialidad del técnico")
    public ResponseEntity<ApiResponse<Void>> actualizarCertificadoEspecialidad(
            @PathVariable UUID idUsuarioTecnico,
            @PathVariable UUID idEspecialidad,
            @Valid @RequestBody ActualizarCertificadoEspecialidadRequest request) {
        especialidadService.actualizarCertificadoEspecialidad(idUsuarioTecnico, idEspecialidad, request.getCertificadoUrl());
        return ResponseEntity.ok(ApiResponse.success("Certificado de especialidad actualizado exitosamente.", null));
    }

    @PatchMapping("/tecnico/{idUsuarioTecnico}/especialidad/{idEspecialidad}/certificate/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprobar certificado de especialidad (Admin)", description = "El admin aprueba el certificado de una especialidad específica del técnico")
    public ResponseEntity<ApiResponse<Void>> aprobarCertificado(
            @PathVariable UUID idUsuarioTecnico,
            @PathVariable UUID idEspecialidad) {
        especialidadService.aprobarCertificado(idUsuarioTecnico, idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Certificado aprobado exitosamente.", null));
    }

    @PatchMapping("/tecnico/{idUsuarioTecnico}/especialidad/{idEspecialidad}/certificate/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechazar certificado de especialidad (Admin)", description = "El admin rechaza el certificado de una especialidad específica del técnico")
    public ResponseEntity<ApiResponse<Void>> rechazarCertificado(
            @PathVariable UUID idUsuarioTecnico,
            @PathVariable UUID idEspecialidad) {
        especialidadService.rechazarCertificado(idUsuarioTecnico, idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Certificado rechazado.", null));
    }

    @GetMapping("/{idEspecialidad}/tecnicos")
    @Operation(summary = "Listar técnicos por especialidad", description = "Obtiene todos los técnicos que tienen una especialidad específica")
    public ResponseEntity<ApiResponse<List<UUID>>> listarTecnicosPorEspecialidad(@PathVariable UUID idEspecialidad) {
        List<UUID> response = especialidadService.listarTecnicosPorEspecialidad(idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Técnicos por especialidad obtenidos exitosamente.", response));
    }

    @GetMapping("/existe/{idEspecialidad}")
    @Operation(summary = "Verificar existencia de especialidad", description = "Verifica si existe una especialidad con el ID proporcionado")
    public ResponseEntity<ApiResponse<Boolean>> existeEspecialidad(@PathVariable UUID idEspecialidad) {
        boolean existe = especialidadService.existeEspecialidad(idEspecialidad);
        return ResponseEntity.ok(ApiResponse.success("Verificación de existencia completada.", existe));
    }

    @GetMapping("/existe/nombre")
    @Operation(summary = "Verificar nombre de especialidad", description = "Verifica si existe una especialidad con el nombre proporcionado")
    public ResponseEntity<ApiResponse<Boolean>> existeEspecialidadPorNombre(@RequestParam String nombre) {
        boolean existe = especialidadService.existeEspecialidadPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success("Verificación de nombre completada.", existe));
    }
}
