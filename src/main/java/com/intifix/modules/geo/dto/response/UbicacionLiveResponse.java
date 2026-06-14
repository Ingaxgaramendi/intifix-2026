package com.intifix.modules.geo.dto.response;

import com.intifix.modules.geo.entity.EstadoLive;
import com.intifix.modules.geo.entity.OrigenUbicacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionLiveResponse {
    private UUID tecnicoUuid;
    private double latitud;
    private double longitud;
    private boolean conectado;
    private Double precision;
    private OrigenUbicacion origen;
    private EstadoLive estado;
    private Instant ultimaActualizacion;
}
