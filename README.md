# Backend - Party Guest API

## Configuration

### Prérequis
- Java 17 ou supérieur
- Maven 3.9+
- Firebase Project avec Firestore activé

### Configuration Firebase
1. Créez un projet Firebase sur https://console.firebase.google.com
2. Activez Cloud Firestore
3. Générez une clé privée (Service Account):
   - Allez dans Project Settings > Service Accounts
   - Cliquez sur "Generate new private key"
4. Téléchargez le fichier JSON
5. Renommez-le en `serviceAccountKey.json`
6. Placez-le dans `src/main/resources/`

### Installation et démarrage

```bash
# Installer les dépendances
./mvnw clean install

# Démarrer l'application
./mvnw spring-boot:run
```

L'API sera accessible sur http://localhost:8080

## Structure du projet

```
src/
├── main/
│   ├── java/com/partyguest/
│   │   ├── config/          # Configuration (Firebase, CORS)
│   │   ├── controller/      # Controllers REST
│   │   ├── model/           # Entités
│   │   ├── repository/      # Accès aux données (Firestore)
│   │   ├── service/         # Logique métier
│   │   └── util/            # Utilitaires (QR Code)
│   └── resources/
│       ├── application.properties
│       └── serviceAccountKey.json  # À créer
```

## API Endpoints

### Étudiants
- `GET /api/etudiant` - Liste des étudiants
- `GET /api/etudiant/{id}/details` - Détails d'un étudiant
- `POST /api/etudiant` - Créer un étudiant
- `PUT /api/etudiant/{id}` - Modifier un étudiant
- `DELETE /api/etudiant/{id}` - Supprimer un étudiant

### Billets
- `GET /api/billet` - Liste des billets
- `GET /api/billet/{id}/details` - Détails d'un billet
- `POST /api/billet` - Créer un billet (génère automatiquement le QR code)
- `PUT /api/billet/{id}` - Modifier un billet
- `DELETE /api/billet/{id}` - Supprimer un billet

### Réservations
- `GET /api/reservation` - Liste des réservations
- `GET /api/reservation/{id}/details` - Détails d'une réservation avec ses détails
- `POST /api/reservation` - Créer une réservation
- `PUT /api/reservation/{id}` - Modifier une réservation
- `DELETE /api/reservation/{id}` - Supprimer une réservation

## Collections Firestore

Le backend crée automatiquement les collections suivantes dans Firestore:
- `etudiants` - Informations sur les étudiants
- `billets` - Billets avec QR codes
- `reservations` - Réservations
- `reservation_details` - Détails des réservations (lien avec étudiants et billets)
- `status` - Statuts des réservations
