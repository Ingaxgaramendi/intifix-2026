package com.intifix.modules.chat.dto.request;

import com.intifix.modules.chat.entity.TipoMensaje;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Envío de un mensaje. Para tipo TEXTO el contenido es obligatorio; para tipos
 * de archivo se exige {@code adjunto} (validado en el servicio). El emisor sale
 * del usuario autenticado, nunca del request (anti-IDOR).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnviarMensajeRequest {

    @NotNull(message = "El idConversacion es obligatorio")
    private UUID idConversacion;

    @Builder.Default
    @NotNull(message = "El tipo de mensaje es obligatorio")
    private TipoMensaje tipo = TipoMensaje.TEXTO;

    @Size(max = 4000, message = "El contenido no puede exceder 4000 caracteres")
    private String contenido;

    @Valid
    private AdjuntoRequest adjunto;

    // Opcional: id del mensaje al que se responde (hilo).
    private UUID idMensajeRespondido;
}
