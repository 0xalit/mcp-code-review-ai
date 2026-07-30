# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy Maven parent POM and common models
COPY pom.xml .
COPY common-models common-models
COPY gateway-service gateway-service

# Build gateway-service jar
RUN mvn clean package -pl gateway-service -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /build/gateway-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
