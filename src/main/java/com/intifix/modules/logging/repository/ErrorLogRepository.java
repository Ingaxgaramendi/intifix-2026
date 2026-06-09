package com.intifix.modules.logging.repository;

import com.intifix.modules.logging.document.ErrorLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorLogRepository extends MongoRepository<ErrorLogDocument, String> {
}
