package com.intifix.modules.technicians.controller;

import com.intifix.modules.technicians.dto.request.ActualizarTecnicoRequest;
import com.intifix.modules.technicians.dto.request.CambiarDisponibilidadRequest;
import com.intifix.modules.technicians.dto.request.CrearTecnicoRequest;
import com.intifix.modules.technicians.dto.response.TecnicoDetalleResponse;
import com.intifix.modules.technicians.dto.response.TecnicoResponse;
import com.intifix.modules.technicians.enums.DisponibilidadTecnico;
import com.intifix.modules.technicians.enums.EstadoAprobacionTecnico;
import com.intifix.modules.technicians.service.TecnicoService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians")
@RequiredArgsConstructor
@Tag(name = "Técnicos", description = "API para gestión de perfiles técnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    @PostMapping
    @Operation(summary = "Crear perfil técnico", description = "Crea un nuevo perfil técnico en el sistema")
    public ResponseEntity<ApiResponse<TecnicoResponse>> crearTecnico(@Valid @RequestBody CrearTecnicoRequest request) {
        TecnicoResponse response = tecnicoService.crearTecnico(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Perfil técnico creado exitosamente.", response));
    }

    @GetMapping("/{idUsuario}")
    @Operation(summary = "Obtener técnico por ID", description = "Obtiene la información básica de un técnico por su ID de usuario")
    public ResponseEntity<ApiResponse<TecnicoResponse>> obtenerTecnicoPorId(@PathVariable UUID idUsuario) {
        TecnicoResponse response = tecnicoService.obtenerTecnicoPorId(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Técnico encontrado.", response));
    }

    @GetMapping("/{idUsuario}/detalle")
    @Operation(summary = "Obtener detalle técnico", description = "Obtiene la información completa de un técnico incluyendo horarios, especialidades y reputación")
    public ResponseEntity<ApiResponse<TecnicoDetalleResponse>> obtenerDetalleTecnicoPorId(@PathVariable UUID idUsuario) {
        TecnicoDetalleResponse response = tecnicoService.obtenerDetalleTecnicoPorId(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Detalle del técnico encontrado.", response));
    }

    @PutMapping("/{idUsuario}")
    @Operation(summary = "Actualizar técnico", description = "Actualiza la información de un técnico existente")
    public ResponseEntity<ApiResponse<TecnicoResponse>> actualizarTecnico(
            @PathVariable UUID idUsuario,
            @Valid @RequestBody ActualizarTecnicoRequest request) {
        TecnicoResponse response = tecnicoService.actualizarTecnico(idUsuario, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil técnico actualizado exitosamente.", response));
    }

    @DeleteMapping("/{idUsuario}")
    @Operation(summary = "Eliminar técnico", description = "Elimina el perfil de un técnico del sistema")
    public ResponseEntity<ApiResponse<Void>> eliminarTecnico(@PathVariable UUID idUsuario) {
        tecnicoService.eliminarTecnico(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Perfil técnico eliminado exitosamente.", null));
    }

    @GetMapping
    @Operation(summary = "Listar todos los técnicos", description = "Obtiene una lista paginada de todos los técnicos")
    public ResponseEntity<ApiResponse<Page<TecnicoResponse>>> obtenerTodosTecnicos(Pageable pageable) {
        Page<TecnicoResponse> response = tecnicoService.obtenerTodosTecnicos(pageable);
        return ResponseEntity.ok(ApiResponse.success("Lista de técnicos obtenida exitosamente.", response));
    }

    @GetMapping("/buscar/dni")
    @Operation(summary = "Buscar técnico por DNI/RUC", description = "Busca un técnico por su número de DNI o RUC")
    public ResponseEntity<ApiResponse<TecnicoResponse>> buscarTecnicoPorDniRuc(@RequestParam String dniRuc) {
        TecnicoResponse response = tecnicoService.buscarTecnicoPorDniRuc(dniRuc);
        return ResponseEntity.ok(ApiResponse.success("Técnico encontrado por DNI/RUC.", response));
    }

    @GetMapping("/buscar/disponibilidad")
    @Operation(summary = "Buscar técnicos por disponibilidad", description = "Obtiene una lista paginada de técnicos filtrados por disponibilidad")
    public ResponseEntity<ApiResponse<Page<TecnicoResponse>>> buscarTecnicosPorDisponibilidad(
            @RequestParam DisponibilidadTecnico disponibilidad,
            Pageable pageable) {
        Page<TecnicoResponse> response = tecnicoService.buscarTecnicosPorDisponibilidad(disponibilidad, pageable);
        return ResponseEntity.ok(ApiResponse.success("Técnicos filtrados por disponibilidad.", response));
    }

    @GetMapping("/buscar/especialidad")
    @Operation(summary = "Buscar técnicos por especialidad", description = "Obtiene una lista paginada de técnicos que tienen una especialidad específica")
    public ResponseEntity<ApiResponse<Page<TecnicoResponse>>> buscarTecnicosPorEspecialidad(
            @RequestParam UUID idEspecialidad,
            Pageable pageable) {
        Page<TecnicoResponse> response = tecnicoService.buscarTecnicosPorEspecialidad(idEspecialidad, pageable);
        return ResponseEntity.ok(ApiResponse.success("Técnicos filtrados por especialidad.", response));
    }

    @GetMapping("/buscar/estado")
    @Operation(summary = "Buscar técnicos por estado de aprobación", description = "Obtiene una lista paginada de técnicos filtrados por estado de aprobación")
    public ResponseEntity<ApiResponse<Page<TecnicoResponse>>> buscarTecnicosPorEstado(
            @RequestParam EstadoAprobacionTecnico estado,
            Pageable pageable) {
        Page<TecnicoResponse> response = tecnicoService.buscarTecnicosPorEstado(estado, pageable);
        return ResponseEntity.ok(ApiResponse.success("Técnicos filtrados por estado de aprobación.", response));
    }

    @PatchMapping("/{idUsuario}/aprobar")
    @Operation(summary = "Aprobar técnico", description = "Aprueba un técnico para que pueda ofrecer servicios")
    public ResponseEntity<ApiResponse<TecnicoResponse>> aprobarTecnico(@PathVariable UUID idUsuario) {
        TecnicoResponse response = tecnicoService.aprobarTecnico(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Técnico aprobado exitosamente.", response));
    }

    @PatchMapping("/{idUsuario}/rechazar")
    @Operation(summary = "Rechazar técnico", description = "Rechaza un técnico para que no pueda ofrecer servicios")
    public ResponseEntity<ApiResponse<TecnicoResponse>> rechazarTecnico(@PathVariable UUID idUsuario) {
        TecnicoResponse response = tecnicoService.rechazarTecnico(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Técnico rechazado exitosamente.", response));
    }

    @PatchMapping("/{idUsuario}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad", description = "Cambia la disponibilidad de un técnico (DISPONIBLE/OCUPADO)")
    public ResponseEntity<ApiResponse<TecnicoResponse>> cambiarDisponibilidad(
            @PathVariable UUID idUsuario,
            @Valid @RequestBody CambiarDisponibilidadRequest request) {
        TecnicoResponse response = tecnicoService.cambiarDisponibilidad(idUsuario, request);
        return ResponseEntity.ok(ApiResponse.success("Disponibilidad cambiada exitosamente.", response));
    }

    @PatchMapping("/{idUsuario}/documentos")
    @Operation(summary = "Actualizar documentos", description = "Actualiza los documentos de un técnico (DNI, antecedentes, certificados)")
    public ResponseEntity<ApiResponse<TecnicoResponse>> actualizarDocumentos(
            @PathVariable UUID idUsuario,
            @Valid @RequestBody ActualizarTecnicoRequest request) {
        TecnicoResponse response = tecnicoService.actualizarDocumentos(idUsuario, request);
        return ResponseEntity.ok(ApiResponse.success("Documentos actualizados exitosamente.", response));
    }

    @GetMapping("/existe/{idUsuario}")
    @Operation(summary = "Verificar existencia de técnico", description = "Verifica si existe un técnico con el ID de usuario proporcionado")
    public ResponseEntity<ApiResponse<Boolean>> existeTecnico(@PathVariable UUID idUsuario) {
        boolean existe = tecnicoService.existeTecnico(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Verificación de existencia completada.", existe));
    }

    @GetMapping("/existe/dni")
    @Operation(summary = "Verificar DNI/RUC", description = "Verifica si existe un técnico con el DNI/RUC proporcionado")
    public ResponseEntity<ApiResponse<Boolean>> existeTecnicoPorDniRuc(@RequestParam String dniRuc) {
        boolean existe = tecnicoService.existeTecnicoPorDniRuc(dniRuc);
        return ResponseEntity.ok(ApiResponse.success("Verificación de DNI/RUC completada.", existe));
    }

    @GetMapping("/total")
    @Operation(summary = "Contar total de técnicos", description = "Obtiene el total de técnicos registrados en el sistema")
    public ResponseEntity<ApiResponse<Long>> contarTotalTecnicos() {
        long total = tecnicoService.contarTotalTecnicos();
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos calculado.", total));
    }

    @GetMapping("/total/aprobados")
    @Operation(summary = "Contar técnicos aprobados", description = "Obtiene el total de técnicos aprobados en el sistema")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosAprobados() {
        long total = tecnicoService.contarTecnicosAprobados();
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos aprobados calculado.", total));
    }

    @GetMapping("/total/activos")
    @Operation(summary = "Contar técnicos activos", description = "Obtiene el total de técnicos aprobados y disponibles")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosActivos() {
        long total = tecnicoService.contarTecnicosActivos();
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos activos calculado.", total));
    }

    @PutMapping("/{idUsuario}/location")
    @Operation(summary = "Asignar ubicación a técnico", description = "Asigna una ubicación principal/base a un técnico")
    public ResponseEntity<ApiResponse<TecnicoResponse>> asignarUbicacion(
            @PathVariable UUID idUsuario,
            @RequestParam UUID idUbicacion) {
        TecnicoResponse response = tecnicoService.asignarUbicacion(idUsuario, idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Ubicación asignada exitosamente al técnico.", response));
    }

    @PatchMapping("/{idUsuario}/location")
    @Operation(summary = "Actualizar ubicación de técnico", description = "Actualiza la ubicación principal/base de un técnico existente")
    public ResponseEntity<ApiResponse<TecnicoResponse>> actualizarUbicacion(
            @PathVariable UUID idUsuario,
            @RequestParam UUID idUbicacion) {
        TecnicoResponse response = tecnicoService.actualizarUbicacion(idUsuario, idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Ubicación actualizada exitosamente.", response));
    }

    @GetMapping("/location/{idUbicacion}")
    @Operation(summary = "Obtener técnicos por ubicación", description = "Obtiene todos los técnicos asociados a una ubicación específica")
    public ResponseEntity<ApiResponse<java.util.List<TecnicoResponse>>> obtenerTecnicosPorUbicacion(
            @PathVariable UUID idUbicacion) {
        java.util.List<TecnicoResponse> response = tecnicoService.obtenerTecnicosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Técnicos por ubicación obtenidos.", response));
    }

    @GetMapping("/location/{idUbicacion}/available")
    @Operation(summary = "Obtener técnicos disponibles por ubicación", description = "Obtiene técnicos disponibles en una ubicación específica")
    public ResponseEntity<ApiResponse<java.util.List<TecnicoResponse>>> obtenerTecnicosDisponiblesPorUbicacion(
            @PathVariable UUID idUbicacion) {
        java.util.List<TecnicoResponse> response = tecnicoService.obtenerTecnicosDisponiblesPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Técnicos disponibles por ubicación obtenidos.", response));
    }

    @GetMapping("/location/{idUbicacion}/approved")
    @Operation(summary = "Obtener técnicos aprobados por ubicación", description = "Obtiene técnicos aprobados en una ubicación específica")
    public ResponseEntity<ApiResponse<java.util.List<TecnicoResponse>>> obtenerTecnicosAprobadosPorUbicacion(
            @PathVariable UUID idUbicacion) {
        java.util.List<TecnicoResponse> response = tecnicoService.obtenerTecnicosAprobadosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Técnicos aprobados por ubicación obtenidos.", response));
    }

    @GetMapping("/location/{idUbicacion}/available-approved")
    @Operation(summary = "Obtener técnicos disponibles y aprobados por ubicación", description = "Obtiene técnicos disponibles y aprobados en una ubicación específica")
    public ResponseEntity<ApiResponse<java.util.List<TecnicoResponse>>> obtenerTecnicosDisponiblesYAprobadosPorUbicacion(
            @PathVariable UUID idUbicacion) {
        java.util.List<TecnicoResponse> response = tecnicoService.obtenerTecnicosDisponiblesYAprobadosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Técnicos disponibles y aprobados por ubicación obtenidos.", response));
    }

    @GetMapping("/location/{idUbicacion}/count")
    @Operation(summary = "Contar técnicos por ubicación", description = "Obtiene el total de técnicos en una ubicación específica")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosPorUbicacion(@PathVariable UUID idUbicacion) {
        long total = tecnicoService.contarTecnicosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos por ubicación calculado.", total));
    }

    @GetMapping("/location/{idUbicacion}/count/available")
    @Operation(summary = "Contar técnicos disponibles por ubicación", description = "Obtiene el total de técnicos disponibles en una ubicación específica")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosDisponiblesPorUbicacion(@PathVariable UUID idUbicacion) {
        long total = tecnicoService.contarTecnicosDisponiblesPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos disponibles por ubicación calculado.", total));
    }

    @GetMapping("/location/{idUbicacion}/count/approved")
    @Operation(summary = "Contar técnicos aprobados por ubicación", description = "Obtiene el total de técnicos aprobados en una ubicación específica")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosAprobadosPorUbicacion(@PathVariable UUID idUbicacion) {
        long total = tecnicoService.contarTecnicosAprobadosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos aprobados por ubicación calculado.", total));
    }

    @GetMapping("/location/{idUbicacion}/count/available-approved")
    @Operation(summary = "Contar técnicos disponibles y aprobados por ubicación", description = "Obtiene el total de técnicos disponibles y aprobados en una ubicación específica")
    public ResponseEntity<ApiResponse<Long>> contarTecnicosDisponiblesYAprobadosPorUbicacion(@PathVariable UUID idUbicacion) {
        long total = tecnicoService.contarTecnicosDisponiblesYAprobadosPorUbicacion(idUbicacion);
        return ResponseEntity.ok(ApiResponse.success("Total de técnicos disponibles y aprobados por ubicación calculado.", total));
    }
}
