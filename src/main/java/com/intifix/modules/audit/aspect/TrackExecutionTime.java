package com.intifix.modules.audit.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mide y publica (Micrometer) el tiempo de ejecución del método anotado.
 * Útil para vigilar latencias de operaciones críticas (observabilidad).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackExecutionTime {
}
