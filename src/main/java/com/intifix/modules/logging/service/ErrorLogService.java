package com.intifix.modules.logging.service;

import com.intifix.modules.logging.document.ErrorLogDocument;
import com.intifix.modules.logging.repository.ErrorLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService {

    private final ErrorLogRepository repository;

    public ErrorLogService(ErrorLogRepository repository) {
        this.repository = repository;
    }

    @Async
    public void log(String message, Throwable ex, String source) {
        String stack = ex == null ? null : stackTraceOf(ex);
        repository.save(new ErrorLogDocument(message, stack, source));
    }

    private static String stackTraceOf(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : ex.getStackTrace()) {
            sb.append(el).append('\n');
            if (sb.length() > 8000) {
                break;
            }
        }
        return sb.toString();
    }
}
