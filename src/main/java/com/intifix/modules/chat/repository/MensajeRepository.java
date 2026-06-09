package com.intifix.modules.chat.repository;

import com.intifix.modules.chat.document.MensajeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MensajeRepository extends MongoRepository<MensajeDocument, String> {
    Page<MensajeDocument> findByConversacionIdOrderByCreatedAtDesc(String conversacionId, Pageable pageable);
}
