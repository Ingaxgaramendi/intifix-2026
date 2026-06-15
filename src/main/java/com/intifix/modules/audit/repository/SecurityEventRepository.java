package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.SecurityEventDocument;
import com.intifix.modules.audit.entity.SecurityReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface SecurityEventRepository extends MongoRepository<SecurityEventDocument, UUID> {

    Page<SecurityEventDocument> findByReason(SecurityReason reason, Pageable pageable);

    Page<SecurityEventDocument> findByEmail(String email, Pageable pageable);

    Page<SecurityEventDocument> findByUserId(UUID userId, Pageable pageable);

    /**
     * Conteo de eventos por email e IP desde un instante: base para detección de
     * fuerza bruta (p. ej. N logins fallidos en los últimos M minutos).
     */
    long countByEmailAndReasonAndTimestampAfter(String email, SecurityReason reason, Instant desde);
}
