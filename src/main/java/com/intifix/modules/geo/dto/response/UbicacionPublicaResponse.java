package com.intifix.modules.geo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionPublicaResponse {
    private UUID idUbicacion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccionTexto;
    private String referencia;
    private BigDecimal latitud;
    private BigDecimal longitud;
}
