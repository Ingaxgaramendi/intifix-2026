package com.intifix.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class PostgresConfig {
    // Si necesitas Beans adicionales de persistencia transaccional van aquí, rey.
}
