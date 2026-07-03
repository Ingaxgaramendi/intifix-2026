package com.intifix.modules.geo.service.impl;

import com.intifix.modules.geo.dto.request.RegistrarUbicacionPublicaRequest;
import com.intifix.modules.geo.dto.response.UbicacionPublicaResponse;
import com.intifix.modules.geo.gateway.GeoPostgresGateway;
import com.intifix.modules.geo.gateway.GeoPostgresGateway.DatosUbicacion;
import com.intifix.modules.geo.gateway.GeoPostgresGateway.UbicacionPublica;
import com.intifix.modules.geo.service.UbicacionService;
import com.intifix.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final GeoPostgresGateway geoPostgresGateway;

    @Override
    public UbicacionPublicaResponse registrar(RegistrarUbicacionPublicaRequest request) {
        DatosUbicacion datos = new DatosUbicacion(
                request.getDepartamento(),
                request.getProvincia(),
                request.getDistrito(),
                request.getDireccionTexto(),
                request.getReferencia(),
                request.getLatitud(),
                request.getLongitud());

        UUID idUbicacion = geoPostgresGateway.crearUbicacion(datos);
        log.info("Ubicación registrada: {}", idUbicacion);

        return UbicacionPublicaResponse.builder()
                .idUbicacion(idUbicacion)
                .departamento(request.getDepartamento())
                .provincia(request.getProvincia())
                .distrito(request.getDistrito())
                .direccionTexto(request.getDireccionTexto())
                .referencia(request.getReferencia())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .build();
    }

    @Override
    public UbicacionPublicaResponse obtenerPorId(UUID idUbicacion) {
        UbicacionPublica u = geoPostgresGateway.obtenerPorId(idUbicacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una ubicación con id: " + idUbicacion));

        return UbicacionPublicaResponse.builder()
                .idUbicacion(u.idUbicacion())
                .departamento(u.departamento())
                .provincia(u.provincia())
                .distrito(u.distrito())
                .direccionTexto(u.direccionTexto())
                .referencia(u.referencia())
                .latitud(u.latitud())
                .longitud(u.longitud())
                .build();
    }
}
