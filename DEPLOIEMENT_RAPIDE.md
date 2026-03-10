# 🚀 Déploiement Rapide - Application Explorations

## 📝 Résumé des étapes

### 1️⃣ Déployer le Backend (10 minutes)

**Sur Render.com (Gratuit) :**

1. Créez un compte sur https://render.com
2. Cliquez sur "New +" → "Web Service"
3. Connectez votre GitHub/GitLab
4. Configuration :
   - **Build Command** : `cd backend && mvn clean package -DskipTests`
   - **Start Command** : `cd backend && java -Dspring.profiles.active=prod -jar target/*.jar`
   - **Disk** : Créez un disk `/data` de 1GB
5. Notez votre URL : `https://votre-app.onrender.com`

✅ **La base SQLite sera créée automatiquement au démarrage !**

---

### 2️⃣ Créer l'APK (15 minutes)

```bash
# 1. Modifier l'URL de production
nano mobile/.env.production
# Remplacez par : VITE_API_URL=https://votre-app.onrender.com

# 2. Lancer le script de build
./build-apk.sh

# 3. Ouvrir Android Studio
cd mobile
npx cap open android

# 4. Dans Android Studio : Build → Build APK
```

**APK générée ici :**
```
mobile/android/app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎯 Serveurs gratuits comparés

| Serveur | Avantages | Limitations |
|---------|-----------|-------------|
| **Render.com** ⭐ | • Simple<br>• 750h/mois gratuit<br>• Disk persistant | • Sleep après 15min inactivité<br>• 512MB RAM |
| **Railway.app** | • Simple<br>• $5 crédits/mois | • Pas de free tier permanent |
| **Fly.io** | • Performant<br>• 3 apps gratuites | • Plus complexe à configurer |

**Recommandation : Render.com** pour sa simplicité et son free tier généreux.

---

## 📱 Tester l'application

1. **Transférez l'APK** vers votre téléphone
2. **Activez "Sources inconnues"** dans les paramètres Android
3. **Installez** l'APK
4. **Connexion** : `directeur` / `directeur123`

---

## ✅ Checklist finale

- [ ] Backend déployé sur Render
- [ ] URL notée : `https://_______.onrender.com`
- [ ] URL mise à jour dans `mobile/.env.production`
- [ ] Build réussi : `./build-apk.sh`
- [ ] APK générée dans `mobile/android/app/build/outputs/apk/`
- [ ] Application testée sur téléphone

---

## 📚 Documentation complète

Consultez [GUIDE_DEPLOIEMENT.md](./GUIDE_DEPLOIEMENT.md) pour :
- Instructions détaillées
- Dépannage
- Configuration avancée
- Publication sur Google Play

---

## 🆘 Problèmes fréquents

### Backend ne démarre pas
```bash
# Vérifier les logs sur Render Dashboard → Logs
# Solution : Vérifier que le disk /data est bien créé
```

### Application ne se connecte pas
```bash
# 1. Tester l'API dans le navigateur
curl https://votre-app.onrender.com/api-docs

# 2. Vérifier l'URL dans l'application
cat mobile/.env.production
```

### Build APK échoue
```bash
# Nettoyer et rebuild
cd mobile/android
./gradlew clean
cd ..
npx cap sync android
```

---

## 🎉 Félicitations !

Votre application est maintenant en ligne ! 🚀

**Contacts :**
- Développeur : MANGALAHY Fenohasina
- Email : mangalahyfenohasina@gmail.com
- WhatsApp : +261 38 43 371 19
