package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PaiementRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "paiements";

    public List<Paiement> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Paiement> paiements = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Paiement paiement = document.toObject(Paiement.class);
            paiement.setId(document.getId());
            paiements.add(paiement);
        }
        return paiements;
    }

    public List<Paiement> findByReservationId(String reservationId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("idReservation", reservationId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Paiement> paiements = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Paiement paiement = document.toObject(Paiement.class);
            paiement.setId(document.getId());
            paiements.add(paiement);
        }
        return paiements;
    }

    public Paiement findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Paiement paiement = document.toObject(Paiement.class);
            paiement.setId(document.getId());
            return paiement;
        }
        return null;
    }

    public Paiement save(Paiement paiement) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(paiement);
        DocumentReference docRef = future.get();
        paiement.setId(docRef.getId());
        return paiement;
    }

    public void update(String id, Paiement paiement) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(paiement);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
