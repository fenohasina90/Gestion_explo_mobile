#!/bin/bash

echo "📱 Construction de l'application mobile pour Android..."

cd mobile

# Vérifier que les dépendances sont installées
if [ ! -d "node_modules" ]; then
    echo "📦 Installation des dépendances..."
    npm install
fi

# Build de l'application
echo "🔨 Build de l'application..."
npm run build

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors du build"
    exit 1
fi

# Synchroniser avec Capacitor
echo "🔄 Synchronisation avec Capacitor..."
npx cap sync android

if [ $? -eq 0 ]; then
    echo "✅ Build terminé avec succès !"
    echo ""
    echo "📍 Prochaine étape :"
    echo "   npx cap open android"
    echo ""
    echo "Puis dans Android Studio :"
    echo "   Build → Build Bundle(s) / APK(s) → Build APK(s)"
    echo ""
    echo "L'APK sera dans :"
    echo "   mobile/android/app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Erreur lors de la synchronisation"
    exit 1
fi
