package com.intifix.modules.payments.service.interfaces;

import com.intifix.modules.payments.dto.request.CrearMetodoPagoRequest;
import com.intifix.modules.payments.dto.response.MetodoPagoResponse;

import java.util.List;
import java.util.UUID;

public interface MetodoPagoService {

    MetodoPagoResponse crearMetodoPago(CrearMetodoPagoRequest request);

    MetodoPagoResponse obtenerMetodoPagoPorId(UUID idMetodoPago);

    MetodoPagoResponse obtenerMetodoPagoPorNombre(String nombre);

    List<MetodoPagoResponse> listarMetodosPago();
}
