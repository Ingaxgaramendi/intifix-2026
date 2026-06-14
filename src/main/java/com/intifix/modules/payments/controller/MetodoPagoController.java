package com.intifix.modules.payments.controller;

import com.intifix.modules.payments.dto.request.CrearMetodoPagoRequest;
import com.intifix.modules.payments.dto.response.MetodoPagoResponse;
import com.intifix.modules.payments.service.interfaces.MetodoPagoService;
import com.intifix.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments/methods")
@RequiredArgsConstructor
@Tag(name = "Métodos de Pago", description = "API para gestión de métodos de pago")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear método de pago", description = "Crea un nuevo método de pago (solo ADMIN)")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> crearMetodoPago(@Valid @RequestBody CrearMetodoPagoRequest request) {
        MetodoPagoResponse response = metodoPagoService.crearMetodoPago(request);
        return ResponseEntity.ok(ApiResponse.<MetodoPagoResponse>builder()
                .success(true)
                .message("Método de pago creado exitosamente")
                .data(response)
                .build());
    }

    @GetMapping("/{idMetodoPago}")
    @Operation(summary = "Obtener método de pago por ID", description = "Obtiene un método de pago por su ID")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> obtenerMetodoPagoPorId(@PathVariable UUID idMetodoPago) {
        MetodoPagoResponse response = metodoPagoService.obtenerMetodoPagoPorId(idMetodoPago);
        return ResponseEntity.ok(ApiResponse.<MetodoPagoResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Obtener método de pago por nombre", description = "Obtiene un método de pago por su nombre")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> obtenerMetodoPagoPorNombre(@PathVariable String nombre) {
        MetodoPagoResponse response = metodoPagoService.obtenerMetodoPagoPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.<MetodoPagoResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "Listar métodos de pago", description = "Lista todos los métodos de pago disponibles")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponse>>> listarMetodosPago() {
        List<MetodoPagoResponse> response = metodoPagoService.listarMetodosPago();
        return ResponseEntity.ok(ApiResponse.<List<MetodoPagoResponse>>builder()
                .success(true)
                .data(response)
                .build());
    }
}
