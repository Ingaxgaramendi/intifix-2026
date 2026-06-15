package com.intifix.modules.audit.aspect;

import com.intifix.modules.audit.config.AuditRequestContext;
import com.intifix.modules.audit.entity.ExceptionLogDocument;
import com.intifix.modules.audit.service.ExceptionLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Captura global de errores: cualquier excepción que escape de un
 * {@code *ServiceImpl} de los módulos de negocio se registra en
 * {@code exception_logs} con su traza completa.
 *
 * <p>Excluye explícitamente el propio módulo audit ({@code !within(...audit..)})
 * para evitar auditar fallos del auditor (recursión).</p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ExceptionAuditAspect {

    private static final int MAX_STACK_CHARS = 8_000;

    private final ExceptionLogService exceptionLogService;

    @AfterThrowing(
            pointcut = "execution(* com.intifix.modules..*Impl.*(..)) && !within(com.intifix.modules.audit..*)",
            throwing = "ex")
    public void registrar(JoinPoint joinPoint, Throwable ex) {
        ExceptionLogDocument log = ExceptionLogDocument.builder()
                .id(UUID.randomUUID())
                .exceptionClass(ex.getClass().getName())
                .message(ex.getMessage())
                .stackTrace(stackTrace(ex))
                .module(moduloDe(joinPoint))
                .userId(AuditRequestContext.currentUserIdOrNull())
                .build();
        exceptionLogService.registrar(log);
    }

    /** Deriva el nombre del módulo del paquete: com.intifix.modules.<módulo>.* */
    private String moduloDe(JoinPoint joinPoint) {
        String paquete = joinPoint.getSignature().getDeclaringType().getPackageName();
        String prefijo = "com.intifix.modules.";
        if (paquete.startsWith(prefijo)) {
            String resto = paquete.substring(prefijo.length());
            int punto = resto.indexOf('.');
            return punto > 0 ? resto.substring(0, punto) : resto;
        }
        return paquete;
    }

    private String stackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String trace = sw.toString();
        return trace.length() > MAX_STACK_CHARS ? trace.substring(0, MAX_STACK_CHARS) : trace;
    }
}
