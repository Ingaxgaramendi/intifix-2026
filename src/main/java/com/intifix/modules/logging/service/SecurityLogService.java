package com.intifix.modules.logging.service;

import com.intifix.modules.logging.document.SecurityLogDocument;
import com.intifix.modules.logging.repository.SecurityLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityLogService {

    private final SecurityLogRepository repository;

    public SecurityLogService(SecurityLogRepository repository) {
        this.repository = repository;
    }

    @Async
    public void log(UUID userId, String action, String ip, boolean success) {
        String uid = userId == null ? null : userId.toString();
        repository.save(new SecurityLogDocument(uid, action, ip, success));
    }
}
