package com.intifix.modules.services.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UbicacionDto {
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccionTexto;
    private String referencia;
    private BigDecimal latitud;
    private BigDecimal longitud;
}
