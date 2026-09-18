# ==========================================================
# Multi-Stage Dockerfile for Arindam Healthcare Platform
# Optimized for Render.com, Railway.app, Fly.io, and AWS
# ==========================================================

# --- Stage 1: Build JAR Artifact ---
FROM maven:3-eclipse-temurin-25 AS builder

WORKDIR /build

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Ultra-Lightweight Production Runtime ---
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root system user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy built artifact from builder stage
COPY --from=builder /build/target/healthcare-platform-1.0.0.jar /app/app.jar
RUN chown -R appuser:appgroup /app

USER appuser

# Cloud environments (Render/Railway/Heroku) inject the PORT environment variable
ENV PORT=8080
EXPOSE 8080

# Production JVM flags with container memory auto-tuning
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
