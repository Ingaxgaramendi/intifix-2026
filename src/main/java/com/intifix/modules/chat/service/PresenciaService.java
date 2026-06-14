package com.intifix.modules.chat.service;

import com.intifix.modules.chat.dto.response.PresenciaResponse;

import java.util.UUID;

/**
 * Presencia en tiempo real (online/offline/última conexión) respaldada por
 * Redis. Estado volátil: nunca se persiste en Mongo. La marca online expira
 * sola (TTL) para tolerar desconexiones sucias sin dejar usuarios "fantasma".
 */
public interface PresenciaService {

    void marcarOnline(UUID idUsuario);

    void marcarOffline(UUID idUsuario);

    boolean estaOnline(UUID idUsuario);

    PresenciaResponse obtenerPresencia(UUID idUsuario);
}
