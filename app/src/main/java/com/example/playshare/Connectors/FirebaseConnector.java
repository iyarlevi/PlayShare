package com.example.playshare.Connectors;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseConnector {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void signIn(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    public void signUp(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

}
