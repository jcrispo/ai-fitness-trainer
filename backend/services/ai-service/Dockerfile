# 1) Build stage: compile with Java 21 JDK
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# copy Maven wrapper, pom, then download deps
COPY mvnw .mvn pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# copy source, compile, and package
COPY src src
RUN ./mvnw clean package -DskipTests

# 2) Runtime stage: run on Java 21 JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# pull in the fat JAR from builder
COPY --from=builder /workspace/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
