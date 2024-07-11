package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Connectors.FirebaseConnector;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailEdt, passwordEdt, passConfirmEdt;
    Button registerButton, cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        emailEdt = findViewById(R.id.registerEmailEdt);
        passwordEdt = findViewById(R.id.registerPasswordEdt);
        passConfirmEdt = findViewById(R.id.passwordConfirmEdt);
        registerButton = findViewById(R.id.registerNewUserButton);
        cancelButton = findViewById(R.id.cancelButton);

        passConfirmEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerButton.performClick();
                return true;
            }
            return false;
        });
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerNewUserButton) {
            String email = emailEdt.getText().toString();
            String password = passwordEdt.getText().toString();
            String passConfirm = passConfirmEdt.getText().toString();

            boolean valid = true;

            if (email.isEmpty()) {
                emailEdt.setError("Email is required");
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (password.isEmpty()) {
                passwordEdt.setError("Password is required");
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (passConfirm.isEmpty()) {
                passConfirmEdt.setError("Password confirmation is required");
                Toast.makeText(this, "Password confirmation is required", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (!valid)
                return;

            if (password.equals(passConfirm)) {
                FirebaseConnector.signUp(email, password, authResult -> {
                    // Handle successful registration
                    handleRegistrationSuccess();
                }, e -> {
                    // Handle failed registration
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        } else if (v.getId() == R.id.cancelButton) {
            finish();
        }
    }

    private void handleRegistrationSuccess() {
        // Handle successful registration
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}