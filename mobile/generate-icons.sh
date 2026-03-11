#!/bin/bash
# Script de génération des icônes pour l'application mobile
# Usage: ./generate-icons.sh

echo "🎨 Génération des icônes de l'application..."

# Vérifier que l'icône source existe
if [ ! -f "resources/icon.png" ]; then
    echo "❌ Erreur: resources/icon.png n'existe pas"
    echo "📝 Veuillez placer votre logo (1024x1024px) dans mobile/resources/icon.png"
    exit 1
fi

echo "✅ Icône source trouvée: resources/icon.png"

# Générer les icônes pour Android avec @capacitor/assets (officiel)
echo "🤖 Génération des icônes Android avec @capacitor/assets..."
npx @capacitor/assets generate --android --iconBackgroundColor '#DD0000' --iconBackgroundColorDark '#DD0000'

echo ""
echo "✅ Icônes générées avec succès !"
echo "📱 Les icônes ont été copiées dans:"
echo "   - android/app/src/main/res/mipmap-*/"
echo ""
echo "📊 Tailles générées:"
ls -lh android/app/src/main/res/mipmap-*/ic_launcher_foreground.png | awk '{print "   - " $9 " (" $5 ")"}'
echo ""
echo "🔄 Prochaines étapes:"
echo "   1. Rebuild l'APK: npm run build && npx cap sync"
echo "   2. Générer APK: cd android && ./gradlew assembleRelease"
echo ""
echo "💡 Pour installer sur téléphone:"
echo "   adb uninstall com.explorateurs.mobile"
echo "   adb install android/app/build/outputs/apk/release/app-release-unsigned.apk"
