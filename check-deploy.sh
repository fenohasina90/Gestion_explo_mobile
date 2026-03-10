#!/bin/bash

echo "🔍 Vérification du projet avant déploiement sur Render.com..."
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

SUCCESS=0
WARNINGS=0
ERRORS=0

# Fonction de vérification
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✅${NC} $2"
        ((SUCCESS++))
    else
        echo -e "${RED}❌${NC} $2"
        echo -e "   ${RED}Fichier manquant: $1${NC}"
        ((ERRORS++))
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✅${NC} $2"
        ((SUCCESS++))
    else
        echo -e "${RED}❌${NC} $2"
        echo -e "   ${RED}Répertoire manquant: $1${NC}"
        ((ERRORS++))
    fi
}

warn() {
    echo -e "${YELLOW}⚠️${NC}  $1"
    ((WARNINGS++))
}

info() {
    echo -e "${GREEN}ℹ️${NC}  $1"
}

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 Vérification du Backend"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

check_file "backend/pom.xml" "Maven POM"
check_file "backend/src/main/resources/application.properties" "Application properties"
check_file "backend/src/main/resources/application-prod.properties" "Application properties (production)"
check_file "backend/src/main/resources/sql/BD_sqlite.sql" "Script SQL de création de base"
check_file "backend/src/main/java/com/explorateur/backend/config/DatabaseInitializer.java" "Database Initializer"
check_file "backend/src/main/java/com/explorateur/backend/config/CorsConfig.java" "Configuration CORS"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📱 Vérification du Mobile"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

check_file "mobile/package.json" "Package.json"
check_file "mobile/.env.production" "Variables d'environnement production"
check_file "mobile/capacitor.config.json" "Configuration Capacitor"
check_dir "mobile/android" "Projet Android"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔧 Vérification de la Configuration"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Vérifier l'URL de production
if [ -f "mobile/.env.production" ]; then
    URL=$(grep "VITE_API_URL" mobile/.env.production | cut -d'=' -f2)
    if [[ $URL == *"localhost"* ]] || [[ $URL == *"192.168"* ]]; then
        warn "URL de production pointe vers localhost/IP locale"
        echo "   Modification nécessaire après déploiement Render"
    else
        info "URL de production configurée: $URL"
    fi
fi

# Vérifier le profil production
if grep -q "spring.profiles.active=prod" backend/src/main/resources/application-prod.properties 2>/dev/null; then
    info "Profil production configuré"
else
    warn "Profil production peut-être absent"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 Fichiers de déploiement"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

check_file "render.yaml" "Configuration Render"
check_file "build-backend.sh" "Script de build backend"
check_file "build-apk.sh" "Script de build APK"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📚 Documentation"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

check_file "RENDER_DEPLOIEMENT.md" "Guide détaillé Render"
check_file "RENDER_EXPRESS.md" "Guide express Render"
check_file "GUIDE_DEPLOIEMENT.md" "Guide général de déploiement"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 Résumé"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo -e "${GREEN}✅ Succès:${NC} $SUCCESS"
echo -e "${YELLOW}⚠️  Avertissements:${NC} $WARNINGS"
echo -e "${RED}❌ Erreurs:${NC} $ERRORS"

echo ""

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Projet prêt pour le déploiement !${NC}"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📝 Prochaines étapes:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "1. Pousser le code sur GitHub :"
    echo "   git add ."
    echo "   git commit -m 'Ready for deployment'"
    echo "   git push origin main"
    echo ""
    echo "2. Déployer sur Render.com :"
    echo "   • Consultez RENDER_EXPRESS.md pour un guide rapide"
    echo "   • Ou RENDER_DEPLOIEMENT.md pour un guide détaillé"
    echo ""
    echo "3. Après déploiement, récupérez l'URL et mettez à jour :"
    echo "   nano mobile/.env.production"
    echo ""
    echo "4. Créez l'APK :"
    echo "   ./build-apk.sh"
    echo ""
    exit 0
else
    echo -e "${RED}❌ Veuillez corriger les erreurs avant de déployer${NC}"
    echo ""
    echo "Pour obtenir de l'aide :"
    echo "  • Consultez RENDER_DEPLOIEMENT.md"
    echo "  • Vérifiez que tous les fichiers nécessaires existent"
    echo ""
    exit 1
fi
