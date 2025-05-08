FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY backend/services backend/services

RUN mvn dependency:go-offline -pl backend/services/ai-service -am -B
RUN mvn clean package -pl backend/services/ai-service -am -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/backend/services/ai-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
