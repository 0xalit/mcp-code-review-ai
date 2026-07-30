# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy Maven parent POM and common modules required for build
COPY pom.xml .
COPY common-models common-models
COPY config-service config-service

# Build config-service jar
RUN mvn clean package -pl config-service -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /build/config-service/target/*.jar app.jar

# Copy configuration repository directory for native profile
COPY config-repo /app/config-repo
ENV CONFIG_REPO_PATH=/app/config-repo

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
