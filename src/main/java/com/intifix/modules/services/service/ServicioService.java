package com.intifix.modules.services.service;

import com.intifix.modules.services.dto.request.ActualizarServicioRequest;
import com.intifix.modules.services.dto.request.CambiarEstadoServicioRequest;
import com.intifix.modules.services.dto.request.CrearServicioRequest;
import com.intifix.modules.services.dto.response.ServicioDetalleResponse;
import com.intifix.modules.services.dto.response.ServicioResponse;
import com.intifix.modules.services.enums.EstadoServicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Servicio operations.
 * 
 * @author INTIFIX Architecture Team
 * @version 2.0
 */
public interface ServicioService {

    ServicioResponse crearServicio(CrearServicioRequest request);

    ServicioResponse actualizarServicio(UUID idServicio, ActualizarServicioRequest request);

    ServicioResponse cambiarEstadoServicio(UUID idServicio, CambiarEstadoServicioRequest request);

    void eliminarServicio(UUID idServicio);

    ServicioResponse obtenerServicioPorId(UUID idServicio);

    ServicioDetalleResponse obtenerDetalleServicioPorId(UUID idServicio);

    Page<ServicioResponse> obtenerServiciosPorCliente(UUID idCliente, Pageable pageable);

    Page<ServicioResponse> obtenerServiciosPorUbicacion(UUID idUbicacion, Pageable pageable);

    Page<ServicioResponse> obtenerServiciosPorEstado(EstadoServicio estado, Pageable pageable);

    /**
     * Marketplace: servicios abiertos (PENDIENTE/COTIZANDO) visibles para técnicos.
     */
    Page<ServicioResponse> obtenerServiciosDisponibles(Pageable pageable);

    Page<ServicioResponse> buscarServiciosPorTitulo(String titulo, Pageable pageable);

    long contarServiciosPorCliente(UUID idCliente);

    long contarServiciosPorEstado(EstadoServicio estado);

    boolean existeServicio(UUID idServicio);
}
