#!/bin/bash

echo "🧹 Nettoyage base de données..."
rm -f ~/.explorateurs-data/explorateurs.db

echo "🚀 Démarrage backend en mode prod..."
echo ""

cd backend

SPRING_DATASOURCE_URL="jdbc:sqlite:$HOME/.explorateurs-data/explorateurs.db" \
SPRING_PROFILES_ACTIVE=prod \
java -jar target/backend-0.0.1-SNAPSHOT.jar 2>&1
