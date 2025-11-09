# ========================================
# WalaDaw - Multi-stage Dockerfile
# Java 25 + Spring Boot 3.x
# ========================================

# Build stage
FROM openjdk:25-jdk-slim AS builder
LABEL maintainer="joseluisgs@gmail.com"
LABEL version="1.0.0"
LABEL description="WalaDaw - Marketplace de segunda mano"

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy source code
COPY src src/

# Give execution permissions and build
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

# Runtime stage  
FROM openjdk:25-jre-slim
LABEL maintainer="joseluisgs@gmail.com"
LABEL description="WalaDaw - Tu tienda de confianza"

# Create non-root user for security
RUN addgroup --system waladaw && adduser --system --group waladaw

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create necessary directories
RUN mkdir -p upload-dir database && \
    chown -R waladaw:waladaw /app

# Switch to non-root user
USER waladaw

# Environment variables for production
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Volumes for persistence
VOLUME ["/app/upload-dir", "/app/database"]

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
