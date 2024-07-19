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

import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailEdt, passwordEdt, passConfirmEdt;
    Button registerButton, cancelButton;
    private final FireStoreConnector database = FireStoreConnector.getInstance();


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
                emailEdt.setError(getString(R.string.email_empty));
                Toast.makeText(this, getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (password.isEmpty()) {
                passwordEdt.setError(getString(R.string.password_empty));
                Toast.makeText(this, getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (passConfirm.isEmpty()) {
                passConfirmEdt.setError(getString(R.string.password_confirm_empty));
                Toast.makeText(this, getString(R.string.password_confirm_empty), Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (!valid)
                return;

            if (password.equals(passConfirm)) {
                FirebaseConnector.signUp(email, password, authResult -> {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("timeStamp", System.currentTimeMillis());
                    database.addDocumentWithCustomId(
                            CollectionsEnum.USERS.getCollectionName(),
                            FirebaseConnector.getCurrentUser().getUid(),
                            data,
                            aVoid -> {
                                // Handle successful registration
                                handleRegistrationSuccess();
                            },
                            e -> {
                                // Handle failed registration
                                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    );
                }, e -> {
                    // Handle failed registration
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                passConfirmEdt.setError(getString(R.string.password_confirm_error));
                Toast.makeText(this, getString(R.string.password_confirm_error), Toast.LENGTH_SHORT).show();
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