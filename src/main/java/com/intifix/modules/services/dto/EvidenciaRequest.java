package com.intifix.modules.services.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EvidenciaRequest {
    private String urlArchivo;
    private String tipo; // "IMAGEN", "VIDEO", "PDF"
    private String descripcion;
    private UUID subidoPor;
}
