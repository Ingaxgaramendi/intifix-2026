package com.intifix.modules.geo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanciaResponse {
    private double origenLat;
    private double origenLng;
    private double destinoLat;
    private double destinoLng;
    private double distanciaKm;
}
