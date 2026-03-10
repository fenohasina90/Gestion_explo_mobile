#!/bin/bash

echo "🧪 Test du backend en mode production (local)..."

cd backend

# Créer le répertoire de données dans le home de l'utilisateur
DB_DIR="$HOME/.explorateurs-data"
mkdir -p "$DB_DIR"

# Construire le JAR si nécessaire
if [ ! -f "target/backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "📦 Build du backend..."
    mvn clean package -DskipTests
fi

# Lancer le backend en mode production avec base dans le home
echo "🚀 Démarrage en mode production..."
echo "📍 Base de données : $DB_DIR/explorateurs.db"
echo "🌐 URL : http://localhost:8080"  
echo "📚 Swagger : http://localhost:8080/swagger-ui.html"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter"
echo ""

SPRING_DATASOURCE_URL="jdbc:sqlite:$DB_DIR/explorateurs.db" \
SPRING_PROFILES_ACTIVE=prod \
java -jar target/backend-0.0.1-SNAPSHOT.jar
