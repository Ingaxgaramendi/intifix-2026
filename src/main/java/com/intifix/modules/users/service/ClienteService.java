package com.intifix.modules.users.service;

import com.intifix.modules.users.dto.request.ActualizarClienteRequest;
import com.intifix.modules.users.dto.request.CrearClienteRequest;
import com.intifix.modules.users.dto.response.ClienteDetalleResponse;
import com.intifix.modules.users.dto.response.ClientePerfilPublicoResponse;
import com.intifix.modules.users.dto.response.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClienteService {

    ClienteResponse crearCliente(CrearClienteRequest request);

    ClienteResponse obtenerClientePorId(UUID idUsuario);

    ClienteDetalleResponse obtenerDetalleClientePorId(UUID idUsuario);

    ClienteResponse actualizarCliente(UUID idUsuario, ActualizarClienteRequest request);

    /** Fija/actualiza la ubicación base guardada del cliente (FK a ubicaciones). */
    ClienteResponse asignarUbicacion(UUID idUsuario, UUID idUbicacion);

    /** Vista pública (para técnicos): datos de confianza sin info sensible. */
    ClientePerfilPublicoResponse obtenerPerfilPublico(UUID idUsuario);

    void eliminarCliente(UUID idUsuario);

    Page<ClienteResponse> obtenerTodosClientes(Pageable pageable);

    Page<ClienteResponse> buscarClientesPorNombre(String nombre, Pageable pageable);

    ClienteResponse buscarClientePorDniRuc(String dniRuc);

    boolean existeCliente(UUID idUsuario);

    boolean existeClientePorDniRuc(String dniRuc);

    long contarTotalClientes();
}
