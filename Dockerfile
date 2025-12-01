# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Instalar Maven
RUN apk add --no-cache maven

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src

# Build do projeto (skip tests para deploy mais rápido)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar JAR do build stage
COPY --from=build /app/target/core-backend-1.0.0-SNAPSHOT.jar app.jar

# Expor porta
EXPOSE 8080

# Comando para executar
ENTRYPOINT ["java", "-jar", "app.jar"]
