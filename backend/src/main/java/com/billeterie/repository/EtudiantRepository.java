package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.Etudiant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class EtudiantRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "etudiants";

    public List<Etudiant> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Etudiant> etudiants = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Etudiant etudiant = document.toObject(Etudiant.class);
            etudiant.setId(document.getId());
            etudiants.add(etudiant);
        }
        return etudiants;
    }

    public Etudiant findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Etudiant etudiant = document.toObject(Etudiant.class);
            etudiant.setId(document.getId());
            return etudiant;
        }
        return null;
    }

    public Etudiant save(Etudiant etudiant) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(etudiant);
        DocumentReference docRef = future.get();
        etudiant.setId(docRef.getId());
        return etudiant;
    }

    public void update(String id, Etudiant etudiant) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(etudiant);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
