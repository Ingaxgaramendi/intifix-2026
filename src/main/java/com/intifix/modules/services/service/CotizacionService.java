package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.CrearCotizacionRequest;
import com.intifix.modules.services.dto.request.ResponderCotizacionRequest;
import com.intifix.modules.services.dto.response.CotizacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Cotizacion operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
public interface CotizacionService {

    CotizacionResponse crearCotizacion(CrearCotizacionRequest request);

    CotizacionResponse responderCotizacion(UUID idCotizacion, ResponderCotizacionRequest request);

    void eliminarCotizacion(UUID idCotizacion);

    CotizacionResponse obtenerCotizacionPorId(UUID idCotizacion);

    Page<CotizacionResponse> obtenerCotizacionesPorServicio(UUID idServicio, Pageable pageable);

    Page<CotizacionResponse> obtenerCotizacionesPorTecnico(UUID idUsuarioTecnico, Pageable pageable);

    Page<CotizacionResponse> obtenerCotizacionesPendientesPorServicio(UUID idServicio, Pageable pageable);

    Page<CotizacionResponse> obtenerCotizacionesPorServicioOrdenadasPorPrecio(UUID idServicio, boolean ascendente, Pageable pageable);

    long contarCotizacionesPorServicio(UUID idServicio);

    long contarCotizacionesPorTecnico(UUID idUsuarioTecnico);

    boolean existeCotizacion(UUID idCotizacion);

    void expirarCotizacionesVencidas();
}
