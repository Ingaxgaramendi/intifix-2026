package com.intifix.modules.services.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a rating for a service.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCalificacionRequest {

    @NotNull(message = "El idServicio es obligatorio")
    private UUID idServicio;

    // idUsuarioTecnico and idCliente removed - obtained from SecurityContextHolder to prevent IDOR

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;

    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comentario;

    @Min(value = 1, message = "La puntualidad mínima es 1")
    @Max(value = 5, message = "La puntualidad máxima es 5")
    private Integer puntualidad;

    @Min(value = 1, message = "El profesionalismo mínimo es 1")
    @Max(value = 5, message = "El profesionalismo máximo es 5")
    private Integer profesionalismo;

    @Min(value = 1, message = "La calidad del trabajo mínima es 1")
    @Max(value = 5, message = "La calidad del trabajo máxima es 5")
    private Integer calidadTrabajo;

    @Min(value = 1, message = "La comunicación mínima es 1")
    @Max(value = 5, message = "La comunicación máxima es 5")
    private Integer comunicacion;

    private Boolean recomendaria;

    private String[] aspectosPositivos;

    private String[] aspectosMejorar;
}
