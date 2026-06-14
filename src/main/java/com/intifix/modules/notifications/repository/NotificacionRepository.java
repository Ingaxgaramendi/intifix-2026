package com.intifix.modules.notifications.repository;

import com.intifix.modules.notifications.entity.NotificacionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends MongoRepository<NotificacionDocument, UUID> {

    Page<NotificacionDocument> findByIdDestinatario(UUID idDestinatario, Pageable pageable);

    Page<NotificacionDocument> findByIdDestinatarioAndLeida(UUID idDestinatario, boolean leida, Pageable pageable);

    long countByIdDestinatarioAndLeida(UUID idDestinatario, boolean leida);

    List<NotificacionDocument> findByIdDestinatarioAndLeida(UUID idDestinatario, boolean leida);
}
