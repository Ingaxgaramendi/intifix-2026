package com.intifix.modules.services.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for rating information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionResponse {

    private UUID idCalificacion;
    private UUID idServicio;
    private UUID idUsuarioTecnico;
    private UUID idCliente;
    private Integer puntuacion;
    private String comentario;
    private Integer puntualidad;
    private Integer profesionalismo;
    private Integer calidadTrabajo;
    private Integer comunicacion;
    private Boolean recomendaria;
    private ZonedDateTime fechaCalificacion;
    private ZonedDateTime fechaActualizacion;
    private String[] aspectosPositivos;
    private String[] aspectosMejorar;
}
