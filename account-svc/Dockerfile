# Multi-stage build for Account Service
FROM maven:3.8.4-openjdk-8-slim AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
COPY account-api/pom.xml account-api/
COPY common/pom.xml common/
COPY account-svc/pom.xml account-svc/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:8-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/account-svc/target/account-svc-*.jar app.jar

# Create a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Expose the port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]