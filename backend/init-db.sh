#!/bin/bash

# Script d'initialisation de la base de données SQLite

DB_PATH="explorateurs.db"
SQL_SCHEMA="src/main/resources/sql/BD_sqlite.sql"

echo "🗄️  Initialisation de la base de données SQLite..."

# Vérifier si sqlite3 est installé
if ! command -v sqlite3 &> /dev/null; then
    echo "❌ sqlite3 n'est pas installé. Installation..."
    sudo apt install sqlite3 -y
fi

# Sauvegarder l'ancienne base si elle existe
if [ -f "$DB_PATH" ]; then
    BACKUP_FILE="${DB_PATH}.backup.$(date +%Y%m%d_%H%M%S)"
    echo "📦 Sauvegarde de l'ancienne base dans $BACKUP_FILE"
    cp "$DB_PATH" "$BACKUP_FILE"
    rm "$DB_PATH"
fi

# Créer la nouvelle base de données
echo "🔨 Création de la base de données avec le schéma..."
sqlite3 "$DB_PATH" < "$SQL_SCHEMA"

if [ $? -eq 0 ]; then
    echo "✅ Base de données créée avec succès dans: $DB_PATH"
    echo ""
    echo "📊 Tables créées:"
    sqlite3 "$DB_PATH" ".tables"
    echo ""
    echo "📏 Taille de la base: $(du -h $DB_PATH | cut -f1)"
else
    echo "❌ Erreur lors de la création de la base de données"
    exit 1
fi
