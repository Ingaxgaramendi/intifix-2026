package com.intifix.modules.geo.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Alta de la ubicación PÚBLICA del técnico (taller/local). Persiste en
 * PostgreSQL ({@code ubicaciones} + {@code perfiles_tecnico.id_ubicacion}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarUbicacionPublicaRequest {

    @NotBlank @Size(max = 100)
    private String departamento;

    @NotBlank @Size(max = 100)
    private String provincia;

    @NotBlank @Size(max = 100)
    private String distrito;

    @NotBlank @Size(max = 255)
    private String direccionTexto;

    @Size(max = 500)
    private String referencia;

    @NotNull
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal latitud;

    @NotNull
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal longitud;
}
