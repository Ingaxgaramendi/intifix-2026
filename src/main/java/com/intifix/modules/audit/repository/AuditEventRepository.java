package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.AuditEventDocument;
import com.intifix.modules.audit.entity.AuditModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditEventRepository extends MongoRepository<AuditEventDocument, UUID> {

    Page<AuditEventDocument> findByModule(AuditModule module, Pageable pageable);

    Page<AuditEventDocument> findByUserId(UUID userId, Pageable pageable);

    Page<AuditEventDocument> findByEventType(String eventType, Pageable pageable);

    Page<AuditEventDocument> findByModuleAndUserId(AuditModule module, UUID userId, Pageable pageable);
}
