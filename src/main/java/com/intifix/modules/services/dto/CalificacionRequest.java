package com.intifix.modules.services.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CalificacionRequest {
    private UUID idCliente;
    private Integer puntuacion; // Validado del 1 al 5 en el Core
    private String comentario;
}
