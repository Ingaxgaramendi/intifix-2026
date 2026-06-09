package com.intifix.modules.notifications.repository;

import com.intifix.modules.notifications.document.NotificacionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificacionRepository extends MongoRepository<NotificacionDocument, String> {
    Page<NotificacionDocument> findByUsuarioIdOrderByCreatedAtDesc(String usuarioId, Pageable pageable);
}
