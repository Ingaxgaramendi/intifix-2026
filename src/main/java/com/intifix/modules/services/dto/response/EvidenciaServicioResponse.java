package com.intifix.modules.services.dto.response;

import com.intifix.modules.services.enums.TipoArchivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for service evidence information.
 * 
 * @author INTIFIX Architecture Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaServicioResponse {

    private UUID idEvidencia;
    private UUID idServicio;
    private String urlArchivo;
    private String nombreArchivo;
    private TipoArchivo tipoArchivo;
    private Long tamanoBytes;
    private String descripcion;
    private UUID subidoPor;
    private ZonedDateTime fechaSubida;
    private String metadatos;
}
