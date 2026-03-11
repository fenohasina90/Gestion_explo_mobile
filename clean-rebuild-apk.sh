#!/bin/bash

echo "🧹 NETTOYAGE COMPLET ET REBUILD PRODUCTION"
echo "=========================================="
echo ""

cd /home/mangalahy/PERSO/AUTRE/Explo/Gestion_explo_mobile/mobile

# Arrêter tous les processus node
echo "🛑 Arrêt des processus Node..."
killall -9 node 2>/dev/null || true

# Nettoyage COMPLET
echo "🧹 Nettoyage des builds..."
rm -rf dist/
rm -rf android/app/build/
rm -rf android/.gradle/
rm -rf node_modules/.vite/
rm -rf .nuxt/
rm -rf .output/

echo ""
echo "📦 Installation des dépendances..."
npm install

echo ""
echo "📋 Vérification de .env.production..."
cat .env.production
echo ""

echo "🏗️  Build en mode PRODUCTION..."
npm run build -- --mode production

echo ""
echo "🔍 Vérification de l'URL dans le build..."
if grep -q "explorateurs-backend.onrender.com" dist/index.html; then
    echo "✅ URL Render trouvée dans index.html"
else
    echo "⚠️  URL Render NON trouvée dans index.html"
fi

echo ""
echo "Recherche dans les assets JS..."
URL_COUNT=$(grep -r "explorateurs-backend.onrender.com" dist/assets/ 2>/dev/null | wc -l)
echo "✅ URL Render trouvée $URL_COUNT fois dans dist/assets/"

LOCALHOST_COUNT=$(grep -r "localhost:8080" dist/ 2>/dev/null | wc -l)
if [ "$LOCALHOST_COUNT" -eq 0 ]; then
    echo "✅ Aucune référence à localhost"
else
    echo "⚠️  ATTENTION: localhost trouvé $LOCALHOST_COUNT fois !"
fi

echo ""
echo "🔄 Synchronisation Capacitor..."
npx cap sync android

echo ""
echo "🔍 Vérification des assets Android..."
ANDROID_URL_COUNT=$(grep -r "explorateurs-backend.onrender.com" android/app/src/main/assets/ 2>/dev/null | wc -l)
echo "✅ URL Render dans assets Android: $ANDROID_URL_COUNT occurrences"

ANDROID_LOCALHOST=$(grep -r "localhost:8080" android/app/src/main/assets/ 2>/dev/null | wc -l)
if [ "$ANDROID_LOCALHOST" -eq 0 ]; then
    echo "✅ Aucun localhost dans assets Android"
else
    echo "⚠️  localhost trouvé dans assets Android!"
fi

echo ""
echo "=========================================="
echo "✅ BUILD TERMINÉ"
echo "=========================================="
echo ""
echo "📱 PROCHAINES ÉTAPES:"
echo ""
echo "1. Ouvrir Android Studio:"
echo "   cd mobile && npx cap open android"
echo ""
echo "2. Dans Android Studio:"
echo "   - Build → Clean Project"
echo "   - Build → Rebuild Project  (ATTENDEZ que ça finisse)"
echo "   - Build → Build Bundle(s) / APK(s) → Build APK(s)"
echo ""
echo "3. APK sera dans:"
echo "   android/app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "4. Sur le téléphone:"
echo "   - DÉSINSTALLEZ l'ancienne app"
echo "   - Installez le nouvel APK"
echo "   - Testez: directeur / directeur123"
echo ""
echo "🔑 Credentials:"
echo "   Username: directeur"
echo "   Password: directeur123"
echo ""
echo "🌐 Pour tester si le backend est accessible du téléphone:"
echo "   Ouvrez dans le navigateur du téléphone:"
echo "   https://explorateurs-backend.onrender.com/swagger-ui.html"
echo ""
