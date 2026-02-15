package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.Billet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class BilletRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "billets";

    public List<Billet> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Billet> billets = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Billet billet = document.toObject(Billet.class);
            billet.setId(document.getId());
            billets.add(billet);
        }
        return billets;
    }

    public Billet findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Billet billet = document.toObject(Billet.class);
            billet.setId(document.getId());
            return billet;
        }
        return null;
    }

    public Billet save(Billet billet) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(billet);
        DocumentReference docRef = future.get();
        billet.setId(docRef.getId());
        return billet;
    }

    public void update(String id, Billet billet) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(billet);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }

    public Billet findByNumero(String numero) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("numero", numero)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            Billet billet = documents.get(0).toObject(Billet.class);
            billet.setId(documents.get(0).getId());
            return billet;
        }
        return null;
    }
}
