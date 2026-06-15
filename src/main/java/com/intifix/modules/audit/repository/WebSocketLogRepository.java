package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.WebSocketAction;
import com.intifix.modules.audit.entity.WebSocketLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebSocketLogRepository extends MongoRepository<WebSocketLogDocument, UUID> {

    Page<WebSocketLogDocument> findByUserId(UUID userId, Pageable pageable);

    Page<WebSocketLogDocument> findByConversationId(UUID conversationId, Pageable pageable);

    Page<WebSocketLogDocument> findByAction(WebSocketAction action, Pageable pageable);
}
