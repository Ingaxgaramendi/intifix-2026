package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.EstadoServicio;
import com.intifix.modules.services.enums.ModalidadServicio;
import com.intifix.modules.services.enums.TipoFecha;
import com.intifix.modules.services.enums.TipoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for basic service information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponse {

    private UUID idServicio;
    private UUID idCliente;
    /** Nombre del cliente solicitante (enriquecido para el listado del técnico). */
    private String nombreCliente;
    private UUID idUbicacion;
    private UUID idEspecialidad;
    private String titulo;
    private String descripcion;
    private List<String> fotos;
    private ModalidadServicio modalidad;
    private TipoSolicitud tipoSolicitud;
    private UUID idTecnicoDirecto;
    private EstadoServicio estado;
    private BigDecimal presupuestoMaximo;
    private TipoFecha tipoFecha;
    private ZonedDateTime fechaProgramada;
    private ZonedDateTime fechaInicioRango;
    private ZonedDateTime fechaFinRango;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime fechaActualizacion;
    /** Distancia en km desde la ubicación del técnico hasta el servicio. Null si el técnico
     *  no tiene ubicación registrada o el servicio es EN_TALLER_TECNICO. */
    private Double distanciaKm;
}
