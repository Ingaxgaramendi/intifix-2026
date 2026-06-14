package com.intifix.modules.geo.dto.response;

import com.intifix.modules.geo.entity.EstadoLive;
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
public class TrackingResponse {
    private UUID tecnicoUuid;
    private boolean conectado;
    private EstadoLive estado;
    private Instant ultimaActualizacion;
}
