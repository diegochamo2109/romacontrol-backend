# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos todo el proyecto
COPY . .

# Compilamos sin tests
RUN mvn clean package -DskipTests

# =========================
# RUN STAGE
# =========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiamos el .jar generado
COPY --from=build /app/target/*.jar app.jar

# Spring Boot usa el puerto 8080
EXPOSE 8080

# Comando para ejecutar
CMD ["java", "-jar", "app.jar"]
