package com.billeterie.util;

import com.google.cloud.firestore.Firestore;
import com.billeterie.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Initialise la base de données avec les données de base
 * S'exécute automatiquement au démarrage de l'application
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private Firestore firestore;

    @Override
    public void run(String... args) throws Exception {
        initializeStatus();
    }

    /**
     * Initialise les statuts par défaut si la collection est vide
     */
    private void initializeStatus() throws Exception {
        // Vérifier si les status existent déjà
        long count = firestore.collection("status").get().get().size();
        
        if (count == 0) {
            System.out.println("🔄 Initialisation des statuts...");
            
            // Créer les status de base
            List<Status> statusList = Arrays.asList(
                new Status(null, "En attente", "pending"),
                new Status(null, "Confirmé", "confirmed"),
                new Status(null, "Annulé", "cancelled"),
                new Status(null, "Présent", "present")
            );
            
            for (Status status : statusList) {
                firestore.collection("status").add(status).get();
                System.out.println("  ✓ Statut créé : " + status.getLibelle());
            }
            
            System.out.println("✅ Statuts initialisés avec succès !");
        } else {
            System.out.println("ℹ️  Les statuts existent déjà (" + count + " statuts trouvés).");
        }
    }
}
