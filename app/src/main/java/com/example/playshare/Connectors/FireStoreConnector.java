package com.example.playshare.Connectors;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FireStoreConnector {
    private static FireStoreConnector instance;
    private final FirebaseFirestore database;

    private FireStoreConnector() {
        this.database = FirebaseFirestore.getInstance();
    }

    public static synchronized FireStoreConnector getInstance() {
        if (instance == null) {
            instance = new FireStoreConnector();
        }
        return instance;
    }

    public void getDocuments(String collectionName, OnSuccessListener<List<Map<String, Object>>> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> documents = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        documents.add(document.getData());
                    }
                    onSuccess.onSuccess(documents);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Error getting documents: " + e.getMessage());
                    onFailure.onFailure(e);
                });
    }

    public void deleteDocument(String collectionName, String documentId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).document(documentId).delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateDocument(String collectionName, String documentId, Map<String, Object> data, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).document(documentId).update(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDocument(String collectionName, String documentId, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSuccess.onSuccess(documentSnapshot.getData());
                    } else {
                        onFailure.onFailure(new Exception("Document not found"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void addDocument(String collectionName, Map<String, Object> data, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).add(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addDocumentWithCustomId(String collectionName, String documentId, Map<String, Object> data, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).document(documentId).set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDocumentReference(String collectionName, String documentId, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        database.collection(collectionName).document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSuccess.onSuccess(documentSnapshot.getReference());
                    } else {
                        onFailure.onFailure(new Exception("Document not found"));
                    }
                })
                .addOnFailureListener(onFailure);
    }
}


