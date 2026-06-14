package com.intifix.modules.users.controller;

import com.intifix.modules.users.dto.request.ActualizarClienteRequest;
import com.intifix.modules.users.dto.request.CrearClienteRequest;
import com.intifix.modules.users.dto.response.ClienteDetalleResponse;
import com.intifix.modules.users.dto.response.ClienteResponse;
import com.intifix.modules.users.service.ClienteService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de perfiles de cliente del marketplace INTIFIX")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or #request.idUsuario == principal.id")
    @Operation(
        summary = "Crear perfil de cliente",
        description = "Crea el perfil de cliente asociado a un usuario ya registrado en el módulo de identidad. "
            + "Valida la existencia del usuario y la unicidad del DNI/RUC."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Perfil creado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Perfil o DNI/RUC ya registrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> crearCliente(
            @Valid @RequestBody CrearClienteRequest request) {
        ClienteResponse response = clienteService.crearCliente(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Perfil de cliente creado exitosamente.", response));
    }

    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN') or #idUsuario == principal.id")
    @Operation(summary = "Obtener perfil de cliente", description = "Recupera el perfil de cliente por su identificador de usuario. Solo el propietario o un administrador.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> obtenerClientePorId(
            @Parameter(description = "Identificador del usuario propietario del perfil") @PathVariable UUID idUsuario) {
        ClienteResponse response = clienteService.obtenerClientePorId(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Cliente encontrado.", response));
    }

    @GetMapping("/{idUsuario}/detalle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener detalle de cliente", description = "Vista extendida del perfil con indicadores de completitud. Uso administrativo.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalle encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<ClienteDetalleResponse>> obtenerDetalleClientePorId(
            @Parameter(description = "Identificador del usuario propietario del perfil") @PathVariable UUID idUsuario) {
        ClienteDetalleResponse response = clienteService.obtenerDetalleClientePorId(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Detalle del cliente encontrado.", response));
    }

    @PatchMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN') or #idUsuario == principal.id")
    @Operation(
        summary = "Actualizar parcialmente un perfil de cliente",
        description = "Actualización parcial (PATCH): solo se modifican los campos presentes en el cuerpo; los ausentes se conservan."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil actualizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DNI/RUC ya registrado por otro cliente", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizarCliente(
            @Parameter(description = "Identificador del usuario propietario del perfil") @PathVariable UUID idUsuario,
            @Valid @RequestBody ActualizarClienteRequest request) {
        ClienteResponse response = clienteService.actualizarCliente(idUsuario, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil de cliente actualizado exitosamente.", response));
    }

    @DeleteMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar perfil de cliente", description = "Elimina definitivamente el perfil de cliente. Operación administrativa.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Perfil eliminado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Void> eliminarCliente(
            @Parameter(description = "Identificador del usuario propietario del perfil") @PathVariable UUID idUsuario) {
        clienteService.eliminarCliente(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar clientes", description = "Listado paginado de perfiles de cliente. Uso administrativo.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Página de clientes"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<Page<ClienteResponse>>> obtenerTodosClientes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ClienteResponse> response = clienteService.obtenerTodosClientes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Lista de clientes obtenida exitosamente.", response));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar clientes por nombre", description = "Búsqueda paginada por coincidencia parcial de nombres (case-insensitive).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resultados de la búsqueda"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetro de búsqueda inválido", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<Page<ClienteResponse>>> buscarClientesPorNombre(
            @Parameter(description = "Texto a buscar dentro de los nombres completos") @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ClienteResponse> response = clienteService.buscarClientesPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda de clientes completada.", response));
    }

    @GetMapping("/buscar/dni")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar cliente por DNI/RUC", description = "Búsqueda exacta por documento de identidad. Uso administrativo (dato personal).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarClientePorDniRuc(
            @Parameter(description = "DNI (8 dígitos) o RUC (11 dígitos)") @RequestParam String dniRuc) {
        ClienteResponse response = clienteService.buscarClientePorDniRuc(dniRuc);
        return ResponseEntity.ok(ApiResponse.success("Cliente encontrado por DNI/RUC.", response));
    }

    @GetMapping("/existe/{idUsuario}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Verificar existencia de perfil", description = "Indica si existe un perfil de cliente para el usuario. Pensado para integración entre módulos.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verificación completada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<Boolean>> existeCliente(
            @Parameter(description = "Identificador del usuario") @PathVariable UUID idUsuario) {
        boolean existe = clienteService.existeCliente(idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Verificación de existencia completada.", existe));
    }

    @GetMapping("/existe/dni")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verificar existencia por DNI/RUC", description = "Indica si el documento ya está registrado. Uso administrativo (dato personal).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verificación completada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<Boolean>> existeClientePorDniRuc(
            @Parameter(description = "DNI (8 dígitos) o RUC (11 dígitos)") @RequestParam String dniRuc) {
        boolean existe = clienteService.existeClientePorDniRuc(dniRuc);
        return ResponseEntity.ok(ApiResponse.success("Verificación de DNI/RUC completada.", existe));
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Contar clientes", description = "Total de perfiles de cliente registrados. Uso administrativo / métricas.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total calculado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> contarTotalClientes() {
        long total = clienteService.contarTotalClientes();
        return ResponseEntity.ok(ApiResponse.success("Total de clientes calculado.", total));
    }
}
