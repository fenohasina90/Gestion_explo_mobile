#!/bin/bash
# Script de préparation du logo Pathfinder
# Convertit automatiquement votre logo en 1024x1024

echo "🎨 Préparation du logo pour l'application..."

# Chemin vers votre logo Pathfinder
SOURCE_LOGO="$1"
OUTPUT_DIR="resources"
OUTPUT_FILE="$OUTPUT_DIR/icon.png"

# Vérifier qu'un fichier source est fourni
if [ -z "$SOURCE_LOGO" ]; then
    echo "❌ Usage: ./prepare-logo.sh chemin/vers/logo-pathfinder.png"
    echo ""
    echo "Exemple:"
    echo "  ./prepare-logo.sh ~/Downloads/pathfinder-logo.png"
    echo "  ./prepare-logo.sh ../image/logo.png"
    exit 1
fi

# Vérifier que le fichier existe
if [ ! -f "$SOURCE_LOGO" ]; then
    echo "❌ Erreur: Le fichier '$SOURCE_LOGO' n'existe pas"
    exit 1
fi

# Créer le dossier resources si nécessaire
mkdir -p "$OUTPUT_DIR"

# Vérifier si ImageMagick est installé
if command -v convert &> /dev/null; then
    echo "✅ ImageMagick détecté"
    echo "🔄 Redimensionnement du logo en 1024x1024..."
    
    # Convertir et redimensionner
    convert "$SOURCE_LOGO" \
        -resize 1024x1024 \
        -gravity center \
        -background white \
        -extent 1024x1024 \
        "$OUTPUT_FILE"
    
    echo "✅ Logo préparé: $OUTPUT_FILE"
    echo "📏 Taille: 1024x1024 pixels"
    
else
    echo "⚠️  ImageMagick non installé"
    echo "📋 Copie du logo tel quel..."
    cp "$SOURCE_LOGO" "$OUTPUT_FILE"
    
    echo "✅ Logo copié: $OUTPUT_FILE"
    echo "⚠️  Vérifiez que la taille est 1024x1024 pixels"
    echo ""
    echo "Pour installer ImageMagick:"
    echo "  sudo apt install imagemagick"
fi

echo ""
echo "🚀 Prochaine étape:"
echo "   ./generate-icons.sh"
