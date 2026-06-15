package com.intifix.modules.audit.repository;

import com.intifix.modules.audit.entity.GeoAction;
import com.intifix.modules.audit.entity.GeoLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeoLogRepository extends MongoRepository<GeoLogDocument, UUID> {

    Page<GeoLogDocument> findByUserId(UUID userId, Pageable pageable);

    Page<GeoLogDocument> findByAction(GeoAction action, Pageable pageable);
}
