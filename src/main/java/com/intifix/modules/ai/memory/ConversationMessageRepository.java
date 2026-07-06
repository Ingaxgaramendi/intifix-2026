package com.intifix.modules.ai.memory;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversationMessageRepository extends MongoRepository<ConversationMessage, String> {

    /** Últimos N mensajes de la conversación (más reciente primero). */
    List<ConversationMessage> findTop20ByConversationIdOrderByCreadoEnDesc(String conversationId);

    /** Borra todos los mensajes de una conversación (reset de historial). */
    void deleteByConversationId(String conversationId);
}
