package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.ApiLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiLogRepository extends MongoRepository<ApiLogDocument, UUID> {

    Page<ApiLogDocument> findByUserId(UUID userId, Pageable pageable);

    Page<ApiLogDocument> findByPath(String path, Pageable pageable);

    Page<ApiLogDocument> findByStatus(int status, Pageable pageable);
}
