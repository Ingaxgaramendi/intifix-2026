package com.intifix.modules.services.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ServicioResponse {
    private UUID idServicio;
    private UUID idCliente;
    private String titulo;
    private String descripcion;
    private String modalidad;
    private String prioridad;
    private String estado;
    private BigDecimal presupuestoMaximo;
    private LocalDateTime fechaProgramada;
    private String direccionCompleta;
}
