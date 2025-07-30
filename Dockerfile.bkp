# Etapa 1: Build com Maven usando Java 8
FROM maven:3.8-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime com Java 8 (imagem leve)
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
