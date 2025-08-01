# Etapa 1: Build com Maven usando Java 8
FROM maven:3.8-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime com Java 8
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/consulta-creditos-api-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
