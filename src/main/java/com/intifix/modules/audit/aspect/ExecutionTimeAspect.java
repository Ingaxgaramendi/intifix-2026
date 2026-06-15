package com.intifix.modules.audit.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Mide el tiempo de ejecución de los métodos anotados con {@link TrackExecutionTime}
 * y lo publica como métrica Micrometer ({@code audit.execution.time}, etiquetada
 * por clase y método), además de dejar traza en el log. Observabilidad sin tocar
 * la lógica de negocio.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionTimeAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(com.intifix.modules.audit.aspect.TrackExecutionTime)")
    public Object medir(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String clazz = signature.getDeclaringType().getSimpleName();
        String metodo = signature.getMethod().getName();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            long nanos = sample.stop(meterRegistry.timer("audit.execution.time", "class", clazz, "method", metodo));
            log.debug("[perf] {}.{} tomó {} ms", clazz, metodo, nanos / 1_000_000);
        }
    }
}
