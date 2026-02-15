package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ReservationRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "reservations";

    public List<Reservation> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Reservation> reservations = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Reservation reservation = document.toObject(Reservation.class);
            reservation.setId(document.getId());
            reservations.add(reservation);
        }
        return reservations;
    }

    public Reservation findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Reservation reservation = document.toObject(Reservation.class);
            reservation.setId(document.getId());
            return reservation;
        }
        return null;
    }

    public Reservation save(Reservation reservation) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(reservation);
        DocumentReference docRef = future.get();
        reservation.setId(docRef.getId());
        return reservation;
    }

    public void update(String id, Reservation reservation) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(reservation);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
