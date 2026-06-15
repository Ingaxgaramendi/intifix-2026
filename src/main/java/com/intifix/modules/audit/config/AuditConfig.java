package com.intifix.modules.audit.config;

import com.intifix.modules.audit.service.ApiLogService;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Infraestructura del módulo de auditoría:
 * <ul>
 *   <li>{@code auditExecutor}: pool propio que aísla las escrituras de auditoría
 *       del hilo de negocio/request. Si se satura, ejecuta en el hilo llamante
 *       ({@code CallerRunsPolicy}) en vez de perder o bloquear indefinidamente.</li>
 *   <li>Registro del {@link AuditLoggingFilter} con orden posterior a la cadena
 *       de Spring Security, para que el {@code SecurityContext} (usuario) ya esté
 *       poblado al registrar la request.</li>
 * </ul>
 */
@Configuration
public class AuditConfig {

    @Bean(name = "auditExecutor")
    public TaskExecutor auditExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("audit-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public FilterRegistrationBean<AuditLoggingFilter> auditLoggingFilterRegistration(ApiLogService apiLogService) {
        FilterRegistrationBean<AuditLoggingFilter> registration =
                new FilterRegistrationBean<>(new AuditLoggingFilter(apiLogService));
        // Justo después de la cadena de seguridad: el SecurityContext ya tiene al usuario.
        registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 10);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
