package com.intifix.modules.logging.repository;

import com.intifix.modules.logging.document.SecurityLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecurityLogRepository extends MongoRepository<SecurityLogDocument, String> {
}
