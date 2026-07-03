package com.intifix.modules.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearConsultaRequest {

    @NotNull(message = "El idTecnico es obligatorio")
    private UUID idTecnico;
}
