package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.EstadoServicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for service assignment information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionServicioResponse {

    private UUID idAsignacion;
    private UUID idServicio;
    private UUID idUsuarioTecnico;
    private UUID idCotizacion;
    private ZonedDateTime fechaAsignacion;
    private ZonedDateTime fechaInicioEstimada;
    private ZonedDateTime fechaInicioReal;
    private ZonedDateTime fechaFinEstimada;
    private ZonedDateTime fechaFinReal;
    private EstadoServicio estadoServicio;
    private String notasTecnico;
    private String notasCliente;
    private Double coordenadaEncuentroLat;
    private Double coordenadaEncuentroLng;
    private String direccionEncuentro;
}
