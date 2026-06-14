package com.intifix.modules.geo.dto.response;

import com.intifix.modules.geo.entity.FuenteUbicacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Técnico encontrado en una búsqueda por cercanía, ya enriquecido y con la
 * distancia al cliente. {@code fuente} indica si la posición provino del GPS en
 * vivo o de la ubicación pública registrada.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoCercanoResponse {
    private UUID tecnicoUuid;
    private String nombresCompletos;
    private BigDecimal tarifaBase;
    private double latitud;
    private double longitud;
    private double distanciaKm;
    private FuenteUbicacion fuente;
    private boolean online;
}
