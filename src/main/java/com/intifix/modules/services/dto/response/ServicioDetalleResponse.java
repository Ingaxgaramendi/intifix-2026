package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for detailed service information including related data.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDetalleResponse {

    private UUID idServicio;
    private UUID idCliente;
    private UUID idUbicacion;
    private String titulo;
    private String descripcion;
    private ModalidadServicio modalidad;
    private PrioridadServicio prioridad;
    private EstadoServicio estado;
    private BigDecimal presupuestoMaximo;
    private ZonedDateTime fechaProgramada;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime fechaActualizacion;
    private ZonedDateTime fechaFinalizacion;
    private String motivoCancelacion;
    private UUID idAsignacion;
    private UUID idUsuarioTecnico;
    private ZonedDateTime fechaAsignacion;
    private ZonedDateTime fechaInicioReal;
    private ZonedDateTime fechaFinReal;
    private List<CotizacionResponse> cotizaciones;
    private List<EvidenciaServicioResponse> evidencias;
    private CalificacionResponse calificacion;
}
