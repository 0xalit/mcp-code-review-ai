# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy Maven parent POM and dependencies
COPY pom.xml .
COPY common-models common-models
COPY eureka-server eureka-server

# Build eureka-server jar
RUN mvn clean package -pl eureka-server -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /build/eureka-server/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
