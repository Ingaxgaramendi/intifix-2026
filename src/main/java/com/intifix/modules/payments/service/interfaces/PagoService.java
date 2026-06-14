package com.intifix.modules.payments.service.interfaces;

import com.intifix.modules.payments.dto.request.CrearPagoRequest;
import com.intifix.modules.payments.dto.request.ProcesarPagoRequest;
import com.intifix.modules.payments.dto.request.ReembolsarPagoRequest;
import com.intifix.modules.payments.dto.response.PagoDetalleResponse;
import com.intifix.modules.payments.dto.response.PagoResponse;
import com.intifix.modules.payments.dto.response.ResumenPagoResponse;

import java.util.List;
import java.util.UUID;

public interface PagoService {

    PagoResponse crearPago(CrearPagoRequest request);

    PagoDetalleResponse procesarPago(ProcesarPagoRequest request);

    PagoResponse confirmarPago(UUID idPago, String transactionId);

    PagoResponse reembolsarPago(ReembolsarPagoRequest request);

    PagoDetalleResponse obtenerPagoPorId(UUID idPago);

    PagoDetalleResponse obtenerPagoPorServicio(UUID idServicio);

    List<PagoResponse> listarPagos();

    List<PagoResponse> listarPagosPorEstado(String estado);

    ResumenPagoResponse obtenerResumenPagos();
}
