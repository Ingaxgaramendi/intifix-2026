package com.intifix.modules.chat.dto.response;

import com.intifix.modules.chat.entity.EstadoMensaje;
import com.intifix.modules.chat.entity.TipoMensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {
    private UUID id;
    private UUID idConversacion;
    private UUID idEmisor;
    private String contenido;
    private TipoMensaje tipo;
    private EstadoMensaje estado;
    private AdjuntoResponse adjunto;
    private UUID idMensajeRespondido;
    private boolean editado;
    private boolean eliminado;
    private Instant leidoEn;
    private Instant creadoEn;
}
