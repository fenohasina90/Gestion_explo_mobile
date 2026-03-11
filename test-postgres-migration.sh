#!/bin/bash

# Script de test de migration PostgreSQL/Supabase
# Usage: ./test-postgres-migration.sh

set -e  # Arrêter en cas d'erreur

echo "🔍 === Test de migration PostgreSQL/Supabase ==="
echo ""

# Vérifier que .env existe
if [ ! -f "backend/.env" ]; then
    echo "❌ Fichier backend/.env non trouvé !"
    echo ""
    echo "👉 Veuillez créer le fichier :"
    echo "   cp backend/.env.example backend/.env"
    echo "   puis éditez-le avec vos vraies valeurs"
    exit 1
fi

echo "✅ Fichier .env trouvé"

# Charger les variables d'environnement
export $(cat backend/.env | grep -v '^#' | xargs)

# Vérifier que DATABASE_URL est défini
if [ -z "$DATABASE_URL" ]; then
    echo "❌ DATABASE_URL n'est pas défini dans backend/.env"
    exit 1
fi

echo "✅ DATABASE_URL défini"
echo ""

# Vérifier que pom.xml contient PostgreSQL
if grep -q "postgresql" backend/pom.xml; then
    echo "✅ PostgreSQL driver trouvé dans pom.xml"
else
    echo "❌ PostgreSQL driver non trouvé dans pom.xml"
    echo "   Vérifiez que vous avez modifié pom.xml"
    exit 1
fi

echo ""
echo "📦 === Clean et rebuild du backend ==="
cd backend

# Clean
mvn clean

# Compile
mvn compile

# Package
mvn package -DskipTests

echo ""
echo "✅ Build réussi !"
echo ""
echo "🚀 === Lancement du backend ==="
echo ""
echo "Le backend va démarrer sur http://localhost:8080"
echo "Appuyez sur Ctrl+C pour arrêter"
echo ""

# Lancer le backend
mvn spring-boot:run
