#!/bin/bash

echo "🧪 Test rapide du backend en mode production..."
echo ""

# Vérifier si le JAR existe
if [ ! -f "backend/target/backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "❌ JAR non trouvé. Lancez d'abord :"
    echo "   cd backend && mvn clean package -DskipTests"
    exit 1
fi

# Créer le répertoire de données
DB_DIR="$HOME/.explorateurs-data"
mkdir -p "$DB_DIR"

echo "📍 Base de données : $DB_DIR/explorateurs.db"
echo "🌐 URL : http://localhost:8080"
echo "📚 Swagger : http://localhost:8080/swagger-ui.html"
echo ""
echo "💡 Test de connexion :"
echo "   Username: directeur"
echo "   Password: directeur123"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter"
echo ""

cd backend

# Lancer avec la config du home
SPRING_DATASOURCE_URL="jdbc:sqlite:$DB_DIR/explorateurs.db" \
SPRING_PROFILES_ACTIVE=prod \
java -jar target/backend-0.0.1-SNAPSHOT.jar
