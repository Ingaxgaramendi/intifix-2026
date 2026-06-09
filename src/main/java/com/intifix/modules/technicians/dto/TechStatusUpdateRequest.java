package com.intifix.modules.technicians.dto;

import lombok.Data;

@Data
public class TechStatusUpdateRequest {
    private String disponibilidad; // 'DISPONIBLE' u 'OCUPADO'
}
