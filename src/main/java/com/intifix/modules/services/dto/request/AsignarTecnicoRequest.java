package com.intifix.modules.services.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Request DTO for assigning a technician to a service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarTecnicoRequest {

    @NotNull(message = "El idUsuarioTecnico es obligatorio")
    private UUID idUsuarioTecnico;

    @NotNull(message = "El idCotizacion es obligatorio")
    private UUID idCotizacion;

    @FutureOrPresent(message = "La fecha de inicio estimada debe ser presente o futura")
    private ZonedDateTime fechaInicioEstimada;

    @Future(message = "La fecha de fin estimada debe ser futura")
    private ZonedDateTime fechaFinEstimada;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notasTecnico;

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
    private Double coordenadaEncuentroLat;

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
    private Double coordenadaEncuentroLng;

    @Size(max = 500, message = "La dirección de encuentro no puede exceder 500 caracteres")
    private String direccionEncuentro;
}
