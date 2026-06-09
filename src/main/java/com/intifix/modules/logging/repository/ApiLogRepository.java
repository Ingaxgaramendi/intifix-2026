package com.intifix.modules.logging.repository;

import com.intifix.modules.logging.document.ApiLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiLogRepository extends MongoRepository<ApiLogDocument, String> {
}
