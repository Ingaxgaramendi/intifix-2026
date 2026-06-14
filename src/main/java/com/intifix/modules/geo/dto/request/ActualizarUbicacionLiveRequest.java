package com.intifix.modules.geo.dto.request;

import com.intifix.modules.geo.entity.EstadoLive;
import com.intifix.modules.geo.entity.OrigenUbicacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ping de ubicación en tiempo real del técnico (GPS del dispositivo). El emisor
 * sale del token; nunca del payload (anti-IDOR).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUbicacionLiveRequest {

    @NotNull @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitud;

    @NotNull @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitud;

    @PositiveOrZero
    private Double precision;

    @NotNull
    private OrigenUbicacion origen;

    private EstadoLive estado;
}
