package com.intifix.modules.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Vista pública del cliente que un técnico puede consultar (estilo inDrive):
 * datos de confianza sin información sensible. NO incluye DNI/RUC, dirección
 * exacta ni referencia: solo distrito/provincia y coordenadas para un mapa
 * aproximado de la zona.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilPublicoResponse {
    private UUID idUsuario;
    private String nombresCompletos;
    private String fotoPerfilUrl;
    private OffsetDateTime creadoEn;
    /** Zona aproximada (no la dirección exacta). */
    private String distrito;
    private String provincia;
    private BigDecimal latitud;
    private BigDecimal longitud;
    /** Servicios publicados por el cliente (señal de actividad/confianza). */
    private long totalServicios;
    private boolean tieneUbicacion;
}
