# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier les fichiers du projet backend
COPY backend/pom.xml .
COPY backend/src ./src

# Build le JAR
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copier le JAR compilé
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
