package com.billeterie.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.billeterie.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class StatusRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "status";

    public List<Status> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Status> statusList = new ArrayList<>();
        
        for (QueryDocumentSnapshot document : documents) {
            Status status = document.toObject(Status.class);
            status.setId(document.getId());
            statusList.add(status);
        }
        return statusList;
    }

    public Status findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            Status status = document.toObject(Status.class);
            status.setId(document.getId());
            return status;
        }
        return null;
    }

    public Status save(Status status) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(status);
        DocumentReference docRef = future.get();
        status.setId(docRef.getId());
        return status;
    }

    public void update(String id, Status status) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.set(status);
        future.get();
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        future.get();
    }
}
