package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.ReservationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ReservationDetailsRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "reservation_details";

    public List<ReservationDetails> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<ReservationDetails> detailsList = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            ReservationDetails details = document.toObject(ReservationDetails.class);
            details.setId(document.getId());
            detailsList.add(details);
        }
        return detailsList;
    }

    public List<ReservationDetails> findByReservationId(String reservationId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("idReservation", reservationId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<ReservationDetails> detailsList = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            ReservationDetails details = document.toObject(ReservationDetails.class);
            details.setId(document.getId());
            detailsList.add(details);
        }
        return detailsList;
    }

    public ReservationDetails findByBilletId(String billetId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("idBillet", billetId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            ReservationDetails details = documents.get(0).toObject(ReservationDetails.class);
            details.setId(documents.get(0).getId());
            return details;
        }
        return null;
    }

    public ReservationDetails findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            ReservationDetails details = document.toObject(ReservationDetails.class);
            details.setId(document.getId());
            return details;
        }
        return null;
    }

    public ReservationDetails save(ReservationDetails details) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(details);
        DocumentReference docRef = future.get();
        details.setId(docRef.getId());
        return details;
    }

    public void update(String id, ReservationDetails details) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(details);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
