package com.intifix.modules.geo.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Búsqueda de técnicos cercanos. La ubicación del cliente es efímera: viaja en
 * la petición y no se persiste en ningún lado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuscarTecnicosRequest {

    @NotNull @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitud;

    @NotNull @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitud;

    @NotNull @Positive @DecimalMax(value = "100.0", message = "El radio no puede exceder 100 km")
    private Double radioKm;

    // Filtros opcionales.
    private UUID idEspecialidad;
}
