package com.intifix.modules.payments.controller;

import com.intifix.modules.payments.dto.request.CrearFacturaRequest;
import com.intifix.modules.payments.dto.response.FacturaResponse;
import com.intifix.modules.payments.service.interfaces.FacturaService;
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
@RequestMapping("/api/v1/payments/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Facturas", description = "API para gestión de facturas electrónicas (solo ADMIN)")
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping
    @Operation(summary = "Crear factura", description = "Crea una nueva factura electrónica")
    public ResponseEntity<ApiResponse<FacturaResponse>> crearFactura(@Valid @RequestBody CrearFacturaRequest request) {
        FacturaResponse response = facturaService.crearFactura(request);
        return ResponseEntity.ok(ApiResponse.<FacturaResponse>builder()
                .success(true)
                .message("Factura creada exitosamente")
                .data(response)
                .build());
    }

    @GetMapping("/{idFactura}")
    @Operation(summary = "Obtener factura por ID", description = "Obtiene una factura por su ID")
    public ResponseEntity<ApiResponse<FacturaResponse>> obtenerFacturaPorId(@PathVariable UUID idFactura) {
        FacturaResponse response = facturaService.obtenerFacturaPorId(idFactura);
        return ResponseEntity.ok(ApiResponse.<FacturaResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/pago/{idPago}")
    @Operation(summary = "Obtener factura por pago", description = "Obtiene la factura asociada a un pago")
    public ResponseEntity<ApiResponse<FacturaResponse>> obtenerFacturaPorPago(@PathVariable UUID idPago) {
        FacturaResponse response = facturaService.obtenerFacturaPorPago(idPago);
        return ResponseEntity.ok(ApiResponse.<FacturaResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/codigo/{codigoComprobante}")
    @Operation(summary = "Obtener factura por código", description = "Obtiene una factura por su código de comprobante")
    public ResponseEntity<ApiResponse<FacturaResponse>> obtenerFacturaPorCodigo(@PathVariable String codigoComprobante) {
        FacturaResponse response = facturaService.obtenerFacturaPorCodigo(codigoComprobante);
        return ResponseEntity.ok(ApiResponse.<FacturaResponse>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "Listar facturas", description = "Lista todas las facturas del sistema")
    public ResponseEntity<ApiResponse<List<FacturaResponse>>> listarFacturas() {
        List<FacturaResponse> response = facturaService.listarFacturas();
        return ResponseEntity.ok(ApiResponse.<List<FacturaResponse>>builder()
                .success(true)
                .data(response)
                .build());
    }

    @GetMapping("/estado/{estadoFiscal}")
    @Operation(summary = "Listar facturas por estado fiscal", description = "Lista las facturas filtradas por estado fiscal")
    public ResponseEntity<ApiResponse<List<FacturaResponse>>> listarFacturasPorEstado(@PathVariable String estadoFiscal) {
        List<FacturaResponse> response = facturaService.listarFacturasPorEstado(estadoFiscal);
        return ResponseEntity.ok(ApiResponse.<List<FacturaResponse>>builder()
                .success(true)
                .data(response)
                .build());
    }
}
