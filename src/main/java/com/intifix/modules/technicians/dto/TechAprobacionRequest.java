package com.intifix.modules.technicians.dto;

import lombok.Data;

@Data
public class TechAprobacionRequest {
    private String estadoAprobacion; // 'APROBADO' o 'RECHAZADO'
}
