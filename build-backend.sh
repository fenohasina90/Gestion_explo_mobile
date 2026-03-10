#!/bin/bash

echo "🚀 Construction du backend pour la production..."

cd backend

# Nettoyer et construire
echo "📦 Compilation avec Maven..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build réussi !"
    echo "📍 JAR créé : backend/target/backend-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "Pour tester localement en mode production :"
    echo "  java -Dspring.profiles.active=prod -jar target/backend-0.0.1-SNAPSHOT.jar"
else
    echo "❌ Erreur lors du build"
    exit 1
fi
