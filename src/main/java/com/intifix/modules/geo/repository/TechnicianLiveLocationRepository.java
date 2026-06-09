package com.intifix.modules.geo.repository;

import com.intifix.modules.geo.document.TechnicianLiveLocationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TechnicianLiveLocationRepository extends MongoRepository<TechnicianLiveLocationDocument, String> {
}
