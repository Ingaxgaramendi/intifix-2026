package com.intifix.modules.services.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServicioRequest {
    private UUID idCliente;
    private String titulo;
    private String descripcion;
    private String modalidad;
    private String prioridad;
    private BigDecimal presupuestoMaximo;
    private LocalDateTime fechaProgramada;
    private UbicacionDto ubicacion;
}
