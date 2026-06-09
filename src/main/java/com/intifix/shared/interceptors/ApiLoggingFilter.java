package com.intifix.shared.interceptors;

import com.intifix.modules.logging.service.ApiLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class ApiLoggingFilter extends OncePerRequestFilter {

    private final ApiLogService apiLogService;

    public ApiLoggingFilter(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        Instant start = Instant.now();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = Instant.now().toEpochMilli() - start.toEpochMilli();
            apiLogService.logHttp(
                    request.getMethod(),
                    request.getRequestURI(),
                    wrappedResponse.getStatus(),
                    durationMs,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent")
            );
            wrappedResponse.copyBodyToResponse();
        }
    }
}
