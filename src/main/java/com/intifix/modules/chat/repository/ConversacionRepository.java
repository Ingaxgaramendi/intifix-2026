package com.intifix.modules.chat.repository;

import com.intifix.modules.chat.document.ConversacionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConversacionRepository extends MongoRepository<ConversacionDocument, String> {
    Optional<ConversacionDocument> findByServicioId(String servicioId);
}
