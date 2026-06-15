package com.intifix.modules.audit.aspect;

import com.intifix.modules.audit.entity.SecurityReason;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un método sensible para registrar su ejecución en {@code security_events}
 * con el motivo indicado (p. ej. {@code REFRESH_TOKEN_INVALID}, {@code IDOR_ATTEMPT}).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackSecurityAction {

    SecurityReason value();
}
