package com.intifix.modules.payments.service.interfaces;

import com.intifix.modules.payments.dto.request.CrearFacturaRequest;
import com.intifix.modules.payments.dto.response.FacturaResponse;

import java.util.List;
import java.util.UUID;

public interface FacturaService {

    FacturaResponse crearFactura(CrearFacturaRequest request);

    FacturaResponse obtenerFacturaPorId(UUID idFactura);

    FacturaResponse obtenerFacturaPorPago(UUID idPago);

    FacturaResponse obtenerFacturaPorCodigo(String codigoComprobante);

    List<FacturaResponse> listarFacturas();

    List<FacturaResponse> listarFacturasPorEstado(String estadoFiscal);
}
