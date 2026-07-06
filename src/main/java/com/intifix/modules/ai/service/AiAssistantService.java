package com.intifix.modules.ai.service;

import com.intifix.modules.ai.dto.ChatRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoRequest;
import com.intifix.modules.ai.dto.PresupuestoSugeridoResponse;
import reactor.core.publisher.Flux;

/**
 * Motor de razonamiento y recomendación de IntiFix.
 */
public interface AiAssistantService {

    /**
     * Procesa el mensaje y emite la respuesta token a token (streaming).
     */
    Flux<String> chatStream(ChatRequest request);

    /**
     * Variante bloqueante: devuelve la respuesta completa.
     */
    String chat(ChatRequest request);

    /**
     * Estima un rango de presupuesto para el servicio descrito.
     */
    PresupuestoSugeridoResponse estimarPresupuesto(PresupuestoSugeridoRequest request);
}
