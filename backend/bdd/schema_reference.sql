-- ============================================
-- SCHÉMA SQL DE RÉFÉRENCE - PROJET BILLETERIE
-- ============================================
-- ⚠️ IMPORTANT : Ce fichier est fourni UNIQUEMENT pour référence
-- Firebase Firestore est une base NoSQL qui ne nécessite PAS de script SQL
-- Les collections se créent automatiquement lors de la première insertion
-- ============================================

-- ============================================
-- Table: Etudiant
-- Description: Stocke les informations des étudiants/invités
-- ============================================
CREATE TABLE Etudiant (
    id VARCHAR(255) PRIMARY KEY,                -- ID auto-généré par Firestore
    numero VARCHAR(50) NOT NULL,                 -- Numéro d'identification (ex: ETU001)
    etu VARCHAR(50),                             -- Numéro ETU
    nom VARCHAR(100) NOT NULL,                   -- Nom de famille
    prenom VARCHAR(100) NOT NULL                 -- Prénom
);

-- Index pour recherche rapide
CREATE INDEX idx_etudiant_numero ON Etudiant(numero);
CREATE INDEX idx_etudiant_etu ON Etudiant(etu);

-- ============================================
-- Table: Billet
-- Description: Stocke les billets et leurs QR codes
-- ============================================
CREATE TABLE Billet (
    id VARCHAR(255) PRIMARY KEY,                -- ID auto-généré par Firestore
    numero VARCHAR(50) NOT NULL UNIQUE,         -- Numéro unique du billet (ex: BILL-ABC12345)
    qrcode TEXT NOT NULL                        -- QR Code encodé en Base64
);

-- Index pour recherche rapide par numéro
CREATE INDEX idx_billet_numero ON Billet(numero);

-- ============================================
-- Table: Status
-- Description: Statuts possibles pour les réservations
-- ============================================
CREATE TABLE Status (
    id VARCHAR(255) PRIMARY KEY,                -- ID auto-généré par Firestore
    libelle VARCHAR(100) NOT NULL,              -- Libellé affiché (ex: "Confirmé")
    value VARCHAR(50) NOT NULL                  -- Valeur technique (ex: "confirmed")
);

-- Index pour recherche rapide par value
CREATE INDEX idx_status_value ON Status(value);

-- ============================================
-- Table: Reservation
-- Description: Stocke les réservations principales
-- ============================================
CREATE TABLE Reservation (
    id VARCHAR(255) PRIMARY KEY,                -- ID auto-généré par Firestore
    date TIMESTAMP NOT NULL                      -- Date et heure de la réservation
);

-- Index pour tri par date
CREATE INDEX idx_reservation_date ON Reservation(date);

-- ============================================
-- Table: Reservation_details
-- Description: Détails des réservations (lien entre étudiant, billet et statut)
-- ============================================
CREATE TABLE Reservation_details (
    id VARCHAR(255) PRIMARY KEY,                -- ID auto-généré par Firestore
    numero VARCHAR(50),                          -- Numéro du détail de réservation
    id_reservation VARCHAR(255) NOT NULL,        -- Référence vers Reservation
    id_etudiant VARCHAR(255) NOT NULL,           -- Référence vers Etudiant
    id_billet VARCHAR(255) NOT NULL,             -- Référence vers Billet
    id_status VARCHAR(255) NOT NULL,             -- Référence vers Status
    
    -- Contraintes de clés étrangères
    FOREIGN KEY (id_reservation) REFERENCES Reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (id_etudiant) REFERENCES Etudiant(id) ON DELETE CASCADE,
    FOREIGN KEY (id_billet) REFERENCES Billet(id) ON DELETE CASCADE,
    FOREIGN KEY (id_status) REFERENCES Status(id) ON DELETE RESTRICT
);

-- Index pour améliorer les performances des jointures
CREATE INDEX idx_reservation_details_reservation ON Reservation_details(id_reservation);
CREATE INDEX idx_reservation_details_etudiant ON Reservation_details(id_etudiant);
CREATE INDEX idx_reservation_details_billet ON Reservation_details(id_billet);
CREATE INDEX idx_reservation_details_status ON Reservation_details(id_status);

-- ============================================
-- DONNÉES INITIALES
-- ============================================

-- Insertion des statuts par défaut
INSERT INTO Status (id, libelle, value) VALUES 
    ('status_pending', 'En attente', 'pending'),
    ('status_confirmed', 'Confirmé', 'confirmed'),
    ('status_cancelled', 'Annulé', 'cancelled'),
    ('status_present', 'Présent', 'present');

-- ============================================
-- EXEMPLES DE DONNÉES DE TEST
-- ============================================

-- Exemple d'étudiant
INSERT INTO Etudiant (id, numero, etu, nom, prenom) VALUES 
    ('etudiant_1', 'ETU001', '12345', 'Dupont', 'Jean');

-- Exemple de billet
INSERT INTO Billet (id, numero, qrcode) VALUES 
    ('billet_1', 'BILL-ABC12345', 'iVBORw0KGgoAAAANSUhEUgAA...');

-- Exemple de réservation
INSERT INTO Reservation (id, date) VALUES 
    ('reservation_1', '2025-02-20 19:00:00');

-- Exemple de détail de réservation
INSERT INTO Reservation_details (id, numero, id_reservation, id_etudiant, id_billet, id_status) VALUES 
    ('detail_1', 'RES001', 'reservation_1', 'etudiant_1', 'billet_1', 'status_confirmed');

-- ============================================
-- REQUÊTES UTILES (RÉFÉRENCE)
-- ============================================

-- Obtenir tous les détails d'une réservation avec les informations complètes
SELECT 
    rd.id,
    rd.numero,
    r.date AS date_reservation,
    e.nom,
    e.prenom,
    e.etu,
    b.numero AS numero_billet,
    s.libelle AS statut
FROM Reservation_details rd
JOIN Reservation r ON rd.id_reservation = r.id
JOIN Etudiant e ON rd.id_etudiant = e.id
JOIN Billet b ON rd.id_billet = b.id
JOIN Status s ON rd.id_status = s.id
WHERE rd.id_reservation = 'reservation_1';

-- Compter le nombre de réservations par statut
SELECT 
    s.libelle,
    COUNT(*) AS nombre
FROM Reservation_details rd
JOIN Status s ON rd.id_status = s.id
GROUP BY s.libelle;

-- Trouver tous les billets non utilisés
SELECT b.*
FROM Billet b
LEFT JOIN Reservation_details rd ON b.id = rd.id_billet
WHERE rd.id IS NULL;

-- Lister toutes les réservations d'un étudiant
SELECT 
    r.date,
    b.numero AS billet,
    s.libelle AS statut
FROM Reservation_details rd
JOIN Reservation r ON rd.id_reservation = r.id
JOIN Billet b ON rd.id_billet = b.id
JOIN Status s ON rd.id_status = s.id
JOIN Etudiant e ON rd.id_etudiant = e.id
WHERE e.numero = 'ETU001'
ORDER BY r.date DESC;

-- ============================================
-- NOTES IMPORTANTES
-- ============================================
/*
1. Ce schéma SQL est fourni UNIQUEMENT comme documentation
   
2. Firebase Firestore utilise une structure NoSQL différente:
   - Les "tables" deviennent des "collections"
   - Les "lignes" deviennent des "documents"
   - Les "foreign keys" sont gérées par des IDs de référence
   - Pas de jointures natives (gérées côté application)

3. Les collections Firestore pour ce projet:
   - etudiants/
   - billets/
   - reservations/
   - reservation_details/
   - status/

4. Les IDs sont générés automatiquement par Firestore

5. Structure d'un document Firestore (exemple pour Etudiant):
   {
     "id": "auto-generated-id",
     "numero": "ETU001",
     "etu": "12345",
     "nom": "Dupont",
     "prenom": "Jean"
   }

6. Pour initialiser les données, utilisez:
   - L'interface Firebase Console
   - Le script DatabaseInitializer.java (fourni)
   - L'API REST du backend

7. Pas besoin d'exécuter ce script SQL !
   Les collections se créent automatiquement quand vous
   insérez le premier document via l'application.
*/

-- ============================================
-- DIAGRAMME ER (Entité-Relation)
-- ============================================
/*
┌─────────────┐
│  Etudiant   │
├─────────────┤
│ id (PK)     │───┐
│ numero      │   │
│ etu         │   │
│ nom         │   │
│ prenom      │   │
└─────────────┘   │
                  │
┌─────────────┐   │     ┌──────────────────┐     ┌─────────────┐
│   Billet    │   │     │ Reservation_det. │     │ Reservation │
├─────────────┤   │     ├──────────────────┤     ├─────────────┤
│ id (PK)     │───┼────→│ id (PK)          │←────│ id (PK)     │
│ numero      │   │     │ numero           │     │ date        │
│ qrcode      │   │     │ id_reservation   │     └─────────────┘
└─────────────┘   │     │ id_etudiant      │
                  └────→│ id_billet        │
                        │ id_status        │
                        └──────────────────┘
                                 │
                                 ↓
                        ┌─────────────┐
                        │   Status    │
                        ├─────────────┤
                        │ id (PK)     │
                        │ libelle     │
                        │ value       │
                        └─────────────┘
*/
