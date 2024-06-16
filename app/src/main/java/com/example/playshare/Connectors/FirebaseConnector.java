package com.example.playshare.Connectors;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseConnector {
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseConnector() {
    }

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static void signOut() {
        firebaseAuth.signOut();
    }

    public static void signIn(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    public static void signUp(String email, String password, OnSuccessListener<AuthResult> onSuccess, OnFailureListener onFailure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

}
