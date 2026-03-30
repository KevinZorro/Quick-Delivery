# Etapa 1: construir el JAR con Gradle
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Etapa 2: crear una imagen liviana para ejecutar la app
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
