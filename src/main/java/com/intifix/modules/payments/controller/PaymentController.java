package com.intifix.modules.payments.controller;

import com.intifix.modules.payments.dto.request.CrearPagoRequest;
import com.intifix.modules.payments.dto.request.ProcesarPagoRequest;
import com.intifix.modules.payments.dto.request.ReembolsarPagoRequest;
import com.intifix.modules.payments.dto.response.PagoDetalleResponse;
import com.intifix.modules.payments.dto.response.PagoResponse;
import com.intifix.modules.payments.dto.response.ResumenPagoResponse;
import com.intifix.modules.payments.service.interfaces.PagoService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API para gestión de pagos")
public class PaymentController {

    private final PagoService pagoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear un nuevo pago", description = "Crea un nuevo pago para un servicio")
    public ResponseEntity<ApiResponse<PagoResponse>> crearPago(@Valid @RequestBody CrearPagoRequest request) {
        PagoResponse response = pagoService.crearPago(request);
        return ResponseEntity.ok(ApiResponse.<PagoResponse>builder()
                .success(true)
                .message("Pago creado exitosamente")
                .data(response)
                .build());
    }

    @PostMapping("/procesar")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Procesar un pago", description = "Procesa un pago a través del provider de pagos")
    public ResponseEntity<ApiResponse<PagoDetalleResponse>> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        PagoDetalleResponse response = pagoService.procesarPago(request);
        return ResponseEntity.ok(ApiResponse.<PagoDetalleResponse>builder()
                .success(true)
                .message("Pago procesado exitosamente")
                .data(response)
                .build());
    }

    @PostMapping("/{idPago}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirmar un pago", description = "Confirma un pago con su transaction ID (simula el webhook del proveedor; solo ADMIN)")
    public ResponseEntity<ApiResponse<PagoResponse>> confirmarPago(
            @PathVariable UUID idPago,
            @RequestParam String transactionId) {
        PagoResponse response = pagoService.confirmarPago(idPago, transactionId);
        return ResponseEntity.ok(ApiResponse.<PagoResponse>builder()
                .success(true)
                .message("Pago confirmado exitosamente")
                .data(response)
                .build());
    }

    @PostMapping("/reembolsar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reembolsar un pago", description = "Reembolsa un pago existente (solo ADMIN)")
    public ResponseEntity<ApiResponse<PagoResponse>> reembolsarPago(@Valid @RequestBody ReembolsarPagoRequest request) {
        PagoResponse response = pagoService.reembolsarPago(request);
        return ResponseEntity.ok(ApiResponse.<PagoResponse>builder()
                .success(true)
                .message("Pago reembolsado exitosamente")
                .data(response)
                .build());
    }

    @GetMapping("/{idPago}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener pago por ID", description = "Obtiene los detalles de un pago (dueño del servicio o ADMIN)")
    public ResponseEntity<ApiResponse<PagoDetalleResponse>> obtenerPagoPorId(@PathVariable UUID idPago) {
        PagoDetalleResponse response = pagoService.obtenerPagoPorId(idPago);
        return ResponseEntity.ok(ApiResponse.<PagoDetalleResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/servicio/{idServicio}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener pago por servicio", description = "Obtiene el pago asociado a un servicio (dueño del servicio o ADMIN)")
    public ResponseEntity<ApiResponse<PagoDetalleResponse>> obtenerPagoPorServicio(@PathVariable UUID idServicio) {
        PagoDetalleResponse response = pagoService.obtenerPagoPorServicio(idServicio);
        return ResponseEntity.ok(ApiResponse.<PagoDetalleResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pagos", description = "Lista todos los pagos del sistema (solo ADMIN)")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listarPagos() {
        List<PagoResponse> response = pagoService.listarPagos();
        return ResponseEntity.ok(ApiResponse.<List<PagoResponse>>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar pagos por estado", description = "Lista los pagos filtrados por estado (solo ADMIN)")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listarPagosPorEstado(@PathVariable String estado) {
        List<PagoResponse> response = pagoService.listarPagosPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.<List<PagoResponse>>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener resumen de pagos", description = "Obtiene un resumen estadístico de los pagos (solo ADMIN)")
    public ResponseEntity<ApiResponse<ResumenPagoResponse>> obtenerResumenPagos() {
        ResumenPagoResponse response = pagoService.obtenerResumenPagos();
        return ResponseEntity.ok(ApiResponse.<ResumenPagoResponse>builder()
                .success(true)
                .data(response)
                .build());
    }
}
