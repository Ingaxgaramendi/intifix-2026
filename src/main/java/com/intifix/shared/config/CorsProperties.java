package com.intifix.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "intifix.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
}

