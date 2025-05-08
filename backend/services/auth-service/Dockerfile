# 1) Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy parent POM and all modules
COPY pom.xml .
COPY backend/services backend/services

# Pre-download dependencies for auth-service (and its dependencies)
RUN mvn dependency:go-offline -pl backend/services/auth-service -am -B

# Build only auth-service
RUN mvn clean package -pl backend/services/auth-service -am -DskipTests -B

# 2) Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR
COPY --from=builder /workspace/backend/services/auth-service/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
