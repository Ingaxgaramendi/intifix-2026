package com.intifix.modules.chat.dto.response;

import com.intifix.modules.chat.entity.EstadoConversacion;
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
public class ConversacionResponse {
    private UUID id;
    private UUID idServicio;
    private UUID idCliente;
    private UUID idTecnico;
    private EstadoConversacion estado;
    private UUID bloqueadaPor;
    private UltimoMensajeResponse ultimoMensaje;
    // No leídos del usuario que consulta (lo calcula/resuelve el servicio).
    private long noLeidos;
    private Instant creadoEn;
    private Instant actualizadoEn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UltimoMensajeResponse {
        private UUID idMensaje;
        private UUID idEmisor;
        private TipoMensaje tipo;
        private String preview;
        private Instant fecha;
    }
}
