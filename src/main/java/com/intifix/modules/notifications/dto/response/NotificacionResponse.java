package com.intifix.modules.notifications.dto.response;

import com.intifix.modules.notifications.entity.TipoNotificacion;
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
public class NotificacionResponse {
    private UUID id;
    private UUID idDestinatario;
    private TipoNotificacion tipo;
    private String titulo;
    private String cuerpo;
    private UUID referenciaId;
    private boolean leida;
    private Instant leidoEn;
    private Instant creadoEn;
}
