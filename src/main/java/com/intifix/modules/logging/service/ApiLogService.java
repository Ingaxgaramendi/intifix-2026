package com.intifix.modules.logging.service;

import com.intifix.modules.logging.document.ApiLogDocument;
import com.intifix.modules.logging.repository.ApiLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ApiLogService {

    private final ApiLogRepository repository;

    public ApiLogService(ApiLogRepository repository) {
        this.repository = repository;
    }

    @Async
    public void logHttp(String method, String path, int status, long durationMs, String ip, String userAgent) {
        ApiLogDocument doc = new ApiLogDocument();
        doc.setMethod(method);
        doc.setPath(path);
        doc.setStatusCode(status);
        doc.setDurationMs(durationMs);
        doc.setIp(ip);
        doc.setUserAgent(userAgent);
        repository.save(doc);
    }
}
