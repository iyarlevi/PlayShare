package com.example.playshare;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    EditText emailEdt, passwordEdt;
    Button loginBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEdt = findViewById(R.id.emailEditText);
        passwordEdt = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(v -> {

            try {

//            String email = emailEdt.getText().toString();
//            String password = passwordEdt.getText().toString();
//            FirebaseConnector firebaseConnector = new FirebaseConnector();
//            firebaseConnector.signIn(email, password, authResult -> {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);

                String email = emailEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            // Handle successful login
                            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failed login
                            Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show();
                        });
//            },e -> {
//                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show();
//            });
        } catch(Exception e){
            Log.e("LoginActivity", "Error: " + e.getMessage());
        }
    });
}
}