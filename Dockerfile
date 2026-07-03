# =============================================================================
# Intifix Backend — imagen de producción (multi-stage)
# =============================================================================
# Etapa 1: build del fat-jar con Maven (usa la caché de .m2 cuando sea posible)
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build
COPY pom.xml .
# Pre-descarga dependencias (capa cacheada si pom.xml no cambia)
RUN mvn dependency:go-offline -B 2>/dev/null || true

COPY src ./src
RUN mvn -B -DskipTests package

# =============================================================================
# Etapa 2: runtime mínimo (sin JDK, sin Maven, sin código fuente)
# =============================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# No-root: usuario de aplicación
RUN addgroup -S intifix && adduser -S intifix -G intifix

# Directorio de trabajo y carpeta de uploads locales
RUN mkdir -p /app/uploads && chown -R intifix:intifix /app

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar
RUN chown intifix:intifix app.jar

USER intifix

EXPOSE 8080

# Health check que usa el actuator /health (siempre público según SecurityConfig)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
