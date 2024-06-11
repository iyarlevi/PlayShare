package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playshare.Connectors.FirebaseConnector;
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

        emailEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT){
                passwordEdt.requestFocus();
                return true;
            }
            return false;
        });

        passwordEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                loginBtn.performClick();
                return true;
            }
            return false;
        });

        loginBtn.setOnClickListener(v -> {

            try {

            String email = emailEdt.getText().toString();
            if (email.isEmpty()) {
                emailEdt.setError("Please enter email");
                emailEdt.requestFocus();
                return;
            }
            String password = passwordEdt.getText().toString();
            if (password.isEmpty()) {
                passwordEdt.setError("Please enter password");
                passwordEdt.requestFocus();
                return;
            }
            FirebaseConnector firebaseConnector = new FirebaseConnector();
            firebaseConnector.signIn(email, password, authResult -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            },e -> {
                if (e.getMessage().contains("email address is badly formatted")) {
                    emailEdt.setError("Please enter a valid email");
                    emailEdt.requestFocus();
                    return;
                }
                if (e.getMessage().contains("password is invalid")) {
                    passwordEdt.setError("Please enter a valid password");
                    passwordEdt.requestFocus();
                    return;
                }
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show();
            });
        } catch(Exception e){
            Log.e("LoginActivity", "Error: " + e.getMessage());
        }
    });
}
}