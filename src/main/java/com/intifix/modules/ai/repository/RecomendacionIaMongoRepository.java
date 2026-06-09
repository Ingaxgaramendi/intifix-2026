package com.intifix.modules.ai.repository;

import com.intifix.modules.ai.document.RecomendacionIaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecomendacionIaMongoRepository extends MongoRepository<RecomendacionIaDocument, String> {
}
