#!/bin/bash

echo "🧹 Nettoyage des builds précédents..."

# Nettoyer les builds (on est déjà dans mobile/)
rm -rf dist/
rm -rf android/app/build/

echo "📱 Build de l'application en MODE PRODUCTION..."

# Installer les dépendances
npm install

# Build en mode PRODUCTION avec les variables d'environnement de production
echo "📦 Build avec mode production (utilise .env.production)..."
npm run build -- --mode production

# Vérifier que le build utilise bien la bonne URL
echo ""
echo "🔍 Vérification de l'URL dans le build..."
if grep -r "explorateurs-backend.onrender.com" dist/ 2>/dev/null | head -1; then
    echo "✅ URL Render trouvée dans le build !"
else
    echo "⚠️  ATTENTION: URL Render non trouvée"
    echo "   Le build utilise peut-être localhost"
    echo ""
    echo "Contenu de .env.production:"
    cat .env.production
    echo ""
fi

echo ""
echo "🔄 Synchronisation avec Capacitor..."
npx cap sync android

echo ""
echo "✅ Build terminé !"
echo ""
echo "📱 Prochaines étapes:"
echo "1. Ouvrez Android Studio : npx cap open android"
echo "2. Build → Build Bundle(s) / APK(s) → Build APK(s)"
echo "3. Attendez la compilation"
echo "4. L'APK sera dans: android/app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "🔑 Credentials de test:"
echo "   Username: directeur"
echo "   Password: directeur123"
