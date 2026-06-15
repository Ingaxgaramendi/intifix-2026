package com.intifix.modules.audit.aspect;

import com.intifix.modules.audit.entity.AuditAction;
import com.intifix.modules.audit.entity.AuditModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un método de negocio para que su ejecución exitosa quede registrada en
 * {@code audit_events} automáticamente (vía {@link AuditableAspect}), sin tener
 * que publicar un evento ni llamar al servicio de auditoría.
 *
 * <p>Ejemplo: {@code @Auditable(module = SERVICES, action = ACEPTAR, resourceType = "Cotizacion")}.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditModule module();

    AuditAction action();

    String resourceType() default "";
}
