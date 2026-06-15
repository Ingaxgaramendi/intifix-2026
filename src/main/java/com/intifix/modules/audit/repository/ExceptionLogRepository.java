package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.ExceptionLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExceptionLogRepository extends MongoRepository<ExceptionLogDocument, UUID> {

    Page<ExceptionLogDocument> findByModule(String module, Pageable pageable);

    Page<ExceptionLogDocument> findByExceptionClass(String exceptionClass, Pageable pageable);

    Page<ExceptionLogDocument> findByUserId(UUID userId, Pageable pageable);
}
